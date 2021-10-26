package com.samtholiya.factory.machine.service.model;

import java.util.List;

public class MachineSummaryResponse {

    public Integer id;
    public String machineKey;
    public String name;
    public List<ParameterSummaryResponse> parameters;

    public MachineSummaryResponse(Integer id, String machineKey, String name, List<ParameterSummaryResponse> parameters) {
        this.id = id;
        this.machineKey = machineKey;
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MachineSummaryResponse m = (MachineSummaryResponse) o;
        return id.equals(m.id) && name.equals(m.name) && machineKey.equals(m.machineKey) && parameters.equals(m.parameters);
    }

    @Override
    public int hashCode() {
        return id;
    }

}
