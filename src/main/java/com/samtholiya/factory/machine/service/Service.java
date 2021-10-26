package com.samtholiya.factory.machine.service;

import com.samtholiya.factory.machine.service.model.MachineRequest;
import com.samtholiya.factory.machine.service.model.ParameterRequest;

import org.springframework.data.domain.Page;

public interface Service {
    public abstract boolean saveParameters(ParameterRequest machineRequest);

    public abstract Page<?> getAllMachine(Integer page, Integer size);

    public abstract Page<?> getAllMachineSummary(Integer page, Integer size, Integer minutes);

    public abstract boolean saveMachine(MachineRequest machineRequest);
}
