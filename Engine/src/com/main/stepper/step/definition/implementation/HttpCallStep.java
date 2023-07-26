package com.main.stepper.step.definition.implementation;

import com.google.gson.JsonObject;
import com.main.stepper.data.DDRegistry;
import com.main.stepper.data.implementation.enumeration.httpprotocol.HttpProtocolEnumData;
import com.main.stepper.data.implementation.enumeration.httpverb.HttpVerbEnumData;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.StepRunResult;
import com.main.stepper.exceptions.data.EnumValueMissingException;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.step.definition.api.AbstractStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;
import okhttp3.*;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class HttpCallStep extends AbstractStepDefinition {

    public HttpCallStep() {
        super("HTTP Call", false);
        addInput(new DataIO("RESOURCE", "Resource Name (include query parameters)", DataNecessity.MANDATORY, DDRegistry.STRING));
        addInput(new DataIO("ADDRESS", "Domain:Port", DataNecessity.MANDATORY, DDRegistry.STRING));
        addInput(new DataIO("PROTOCOL", "Protocol", DataNecessity.MANDATORY, DDRegistry.HTTP_PROTOCOL_ENUM));
        addInput(new DataIO("METHOD", "Method", DataNecessity.OPTIONAL, DDRegistry.HTTP_VERB_ENUM));
        addInput(new DataIO("BODY", "Request Body", DataNecessity.OPTIONAL, DDRegistry.JSON));
        addOutput(new DataIO("CODE", "Response Code", DDRegistry.NUMBER));
        addOutput(new DataIO("RESPONSE_BODY", "Response Body", DDRegistry.STRING));
    }

    @Override
    public Class<? extends AbstractStepDefinition> getStepClass() {
        return HttpCallStep.class;
    }

    @Override
    public IStepRunResult execute(IStepExecutionContext context) {
        Instant startTime = Instant.now();

        // Read inputs and outputs
        List<IDataIO> inputs = getInputs();
        String resource = (String) context.getInput(inputs.get(0), DDRegistry.STRING.getType());
        String address = (String) context.getInput(inputs.get(1), DDRegistry.STRING.getType());
        HttpProtocolEnumData protocol = (HttpProtocolEnumData) context.getInput(inputs.get(2), DDRegistry.HTTP_PROTOCOL_ENUM.getType());
        HttpVerbEnumData method = (HttpVerbEnumData) context.getInput(inputs.get(3), DDRegistry.HTTP_VERB_ENUM.getType());
        if (method == null) {
            method = new HttpVerbEnumData();
            try {
                method.setValue("GET");
            } catch (EnumValueMissingException ignored) {
            }
        }
        JsonObject body = (JsonObject) context.getInput(inputs.get(4), DDRegistry.JSON.getType());

        String bodyString = (body == null ? "" : body.toString());

        List<IDataIO> outputs = getOutputs();
        IDataIO code = outputs.get(0);
        IDataIO responseBody = outputs.get(1);

        // check if all mandatory data is present and valid
        if (resource == null || address == null || protocol == null) {
            // cancel step
            context.log("Missing mandatory data.");
            context.setOutput(code, null);
            context.setOutput(responseBody, null);
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.FAILURE, startTime, Duration.between(startTime, Instant.now()), "Missing mandatory data.");
        }

        // ensure address doesn't end with '/' and resource doesn't start with '/'
        if (address.endsWith("/")) address = address.substring(0, address.length() - 1);
        if (resource.startsWith("/")) resource = resource.substring(1);

        // create url string
        String url = protocol + "://" + address + "/" + resource;

        // log before making call
        context.log("About to invoke http request: (" + method + ") " + url);

        // make http client
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder()
                .url(url);
        if (method.getValue().get().equals("GET"))
            builder.get();
        else
            builder.method(method.toString(), RequestBody.create(bodyString, MediaType.parse("application/json")));

        Request request = builder.build();

        String summary;
        StepResult result;
        try (Response response = client.newCall(request).execute()) {
            result = StepResult.SUCCESS;
            summary = "Successfully made http call.";
            context.log("Received response. Status code: " + response.code());
            context.setOutput(code, response.code());
            context.setOutput(responseBody, response.body().string());
        } catch (IOException e) {
            result = StepResult.FAILURE;
            context.log("Error while making http call.");
            summary = "Error while making http call.";
            context.setOutput(code, null);
            context.setOutput(responseBody, null);
        }

        client.dispatcher().executorService().shutdown();

        // return result
        return new StepRunResult(context.getUniqueRunId(), getName(), result, startTime, Duration.between(startTime, Instant.now()), summary);
    }
}
