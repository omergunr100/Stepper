package com.main.stepper.step.definition.implementation;

import com.google.gson.JsonObject;
import com.main.stepper.data.DDRegistry;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.StepRunResult;
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
        addInput(new DataIO("RESOURCE", "Resource Name (include query parameters)", DDRegistry.STRING));
        addInput(new DataIO("ADDRESS", "Domain:Port", DDRegistry.STRING));
        addInput(new DataIO("PROTOCOL", "Protocol", DDRegistry.HTTP_PROTOCOL_ENUM));
        addInput(new DataIO("METHOD", "Method", DDRegistry.HTTP_VERB_ENUM));
        addInput(new DataIO("BODY", "Request Body", DDRegistry.JSON));
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
        String protocol = (String) context.getInput(inputs.get(2), DDRegistry.HTTP_PROTOCOL_ENUM.getType());
        String method = (String) context.getInput(inputs.get(3), DDRegistry.HTTP_VERB_ENUM.getType());
        method = (method == null ? "GET" : method);
        JsonObject body = (JsonObject) context.getInput(inputs.get(4), DDRegistry.JSON.getType());

        String bodyString = (body == null ? "" : body.getAsString());

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

        // create url string
        String url = protocol + "://" + address + "/" + resource;

        // log before making call
        context.log("About to invoke http request: (" + method + ") " + url);

        // make http client
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .method(method, RequestBody.create(bodyString, MediaType.parse("application/json")))
                .build();
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

        // return result
        return new StepRunResult(context.getUniqueRunId(), getName(), result, startTime, Duration.between(startTime, Instant.now()), summary);
    }
}
