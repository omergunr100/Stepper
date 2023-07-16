package com.main.stepper.shared.structures.flow;

import com.main.stepper.logger.api.ILogger;
import com.main.stepper.logger.implementation.maplogger.MapLogger;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.gson.FixerUtility;
import com.main.stepper.shared.structures.step.StepRunResultDTO;
import com.main.stepper.shared.structures.step.StepUsageDTO;

import java.util.*;

public class FlowExecutionContextDTO {
    private UUID runId;
    private MapLogger logger;
    private HashMap<StepUsageDTO, HashMap<DataIODTO, DataIODTO>> mappings;
    private HashMap<DataIODTO, Object> variables;
    private ArrayList<StepRunResultDTO> stepRunResults;
    private String user;

    public FlowExecutionContextDTO(UUID runId, ILogger logger, Map<StepUsageDTO, HashMap<DataIODTO, DataIODTO>> mappings, Map<DataIODTO, Object> variables, List<StepRunResultDTO> stepRunResults, String user) {
        this.runId = runId;
        this.logger = (MapLogger) logger;
        this.mappings = new HashMap<>(mappings);
        this.variables = new HashMap<>(variables);
        this.stepRunResults = new ArrayList<>(stepRunResults);
        this.user = user;
    }

    public UUID runId() {
        return runId;
    }

    public ILogger getLogger() {
        return logger;
    }

    public DataIODTO getVariableIOByName(String name) {
        Optional<DataIODTO> io = variables.keySet().stream().filter(dataIO -> dataIO.name().equals(name)).findFirst();
        return io.orElse(null);
    }

    public <T> T getVariable(DataIODTO name, Class<T> type) {
        if (variables.get(name) == null)
            return null;
        return type.cast(variables.get(name));
    }

    public Map<DataIODTO, Object> variables() {
        return variables;
    }

    public String user() {
        return user;
    }

    public FlowExecutionContextDTO fix() {
        FixerUtility.fixMap(variables);
        return this;
    }
}
