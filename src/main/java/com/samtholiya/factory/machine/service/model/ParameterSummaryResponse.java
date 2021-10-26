package com.samtholiya.factory.machine.service.model;

public class ParameterSummaryResponse {
    public String name;
    public Float minimum;
    public Float maximum;
    public Float average;
    public Float median;

    public ParameterSummaryResponse(String name, Float minimum, Float maximum, Float average, Float median) {
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
        this.average = average;
        this.median = median;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ParameterSummaryResponse sr = (ParameterSummaryResponse) o;
        return name.equals(sr.name) && minimum.equals(sr.minimum) && maximum.equals(sr.maximum)
                && average.equals(sr.average) && median.equals(sr.median);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + minimum.hashCode() + maximum.hashCode() + average.hashCode() + median.hashCode();
    }
}
