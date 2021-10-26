package com.samtholiya.factory.machine.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.samtholiya.factory.machine.service.repository.Machine;
import com.samtholiya.factory.machine.service.repository.MachineRepository;
import com.samtholiya.factory.machine.service.repository.Parameter;
import com.samtholiya.factory.machine.service.repository.ParameterRepository;
import com.samtholiya.factory.machine.service.model.ParameterRequest;
import com.samtholiya.factory.machine.service.model.MachineRequest;
import com.samtholiya.factory.machine.service.model.MachineSummaryResponse;
import com.samtholiya.factory.machine.service.model.ParameterSummaryResponse;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

    
    private static final Logger LOG = LoggerFactory.logger(ServiceImpl.class);

    @Autowired
    MachineRepository machineRepository;

    @Autowired
    ParameterRepository parameterRepository;

    @Override
    public boolean saveMachine(MachineRequest machineRequest) {
        try {
            this.machineRepository.save(new Machine(machineRequest.key, machineRequest.name));
        } catch (Exception e) {
            LOG.error(e);
            return false;
        }
        return true;
    }

    @Override
    public boolean saveParameters(ParameterRequest parameterRequest) {
        var machine = this.machineRepository.findByMachineKey(parameterRequest.machineKey);
        if (!machine.isPresent()) {
            return false;
        }
        ArrayList<Parameter> parameters = new ArrayList<>();
        for (var entry : parameterRequest.parameters.entrySet()) {
            parameters.add(new Parameter(machine.get(), entry.getKey(), entry.getValue()));
        }
        this.parameterRepository.saveAll(parameters);
        return true;
    }

    @Override
    public Page<?> getAllMachine(Integer page, Integer size) {
        var machines = this.machineRepository.findAll(PageRequest.of(page, size));
        return machines;
    }

    @Override
    public Page<?> getAllMachineSummary(Integer page, Integer size, Integer minutes) {
        var machines = this.machineRepository.findAll(PageRequest.of(page, size));
        ArrayList<Machine> machineList = new ArrayList<>(machines.get().collect(Collectors.toList()));
        ArrayList<MachineSummaryResponse> msList = new ArrayList<>();
        for (var machine : machineList) {
            var summary = this.getParametersSummary(machine, minutes);
            msList.add(
                    new MachineSummaryResponse(machine.getID(), machine.getMachineKey(), machine.getName(), summary));
        }
        Page<MachineSummaryResponse> msPage = new PageImpl<MachineSummaryResponse>(msList, machines.getPageable(),
                machines.getTotalElements());
        return msPage;
    }

    private List<ParameterSummaryResponse> getParametersSummary(Machine machine, Integer minutes) {
        LocalDateTime time = LocalDateTime.now().minus(minutes, ChronoUnit.MINUTES);
        var summary = this.parameterRepository.findParameterSummaryForMachine(machine, time);
        ArrayList<ParameterSummaryResponse> list = new ArrayList<ParameterSummaryResponse>();
        for (var entity : summary) {
            var median = this.getMedian(machine, (String) entity.get("name"), time);
            list.add(new ParameterSummaryResponse((String) entity.get("name"), (Float) entity.get("minimum"),
                    (Float) entity.get("maximum"), (Float) ((Double) entity.get("average")).floatValue(), median));
        }
        return list;
    }

    private Float getMedian(Machine machine, String name, LocalDateTime date) {
        var parameters = this.parameterRepository.findParameterAfterDateTime(machine, name, date);
        Integer count = parameters.size();
        if (count == 0)
            return 0.0f;
        if (count % 2 != 0) {
            return (float) parameters.get(count / 2).getValue().floatValue();
        }
        return ((float) parameters.get(count / 2).getValue().floatValue()
                + (float) parameters.get((count / 2) - 1).getValue().floatValue()) / 2.0f;
    }

}
