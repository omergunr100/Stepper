package com.main.stepper.shared.structures.step;

import com.main.stepper.io.api.IDataIO;
import com.main.stepper.logger.api.ILogger;
import com.main.stepper.logger.implementation.data.Log;
import com.main.stepper.logger.implementation.maplogger.MapLogger;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.gson.FixerUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StepExecutionContextDTO {
    private UUID runId;
    private HashMap<DataIODTO, DataIODTO> mapping;
    private HashMap<DataIODTO, Object> variables;
    private MapLogger logger;

    public StepExecutionContextDTO(UUID runId, Map<DataIODTO, DataIODTO> mapping, Map<DataIODTO, Object> variables, ILogger logger) {
        this.runId = runId;
        this.mapping = new HashMap<>(mapping);
        this.variables = new HashMap<>(variables);
        this.logger = (MapLogger) logger;
    }

    public UUID runId() {
        return runId;
    }

    public List<Log> getLogs() {
        return logger.getLog();
    }

    public DataIODTO getAliasedDataIO(DataIODTO dataIO) {
        return mapping.get(dataIO);
    }

    public <T> T getInput(DataIODTO name, Class<T> type) {
        DataIODTO alias = mapping.get(name);

        if(!variables.containsKey(alias)){
            return null;
        }

        if(variables.get(alias) == null)
            return null;

        if(!type.isAssignableFrom(variables.get(alias).getClass())){
            return null;
        }
        return type.cast(variables.get(alias));
    }

    public StepExecutionContextDTO fix() {
        FixerUtility.fixMap(variables);
        return this;
    }

}
