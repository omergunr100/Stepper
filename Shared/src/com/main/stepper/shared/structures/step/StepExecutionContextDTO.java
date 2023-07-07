package com.main.stepper.shared.structures.step;

import com.main.stepper.logger.api.ILogger;
import com.main.stepper.logger.implementation.data.Log;
import com.main.stepper.logger.implementation.maplogger.MapLogger;
import com.main.stepper.shared.structures.dataio.DataIODTO;

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
}
