package com.samtholiya.factory.machine.service.repository;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Where;

@Entity
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true)
    private String machineKey;

    private String name;

    @OneToMany(mappedBy = "machine", fetch = FetchType.LAZY)
    @Where(clause = "id in (select max(p.id) from parameter p group by p.machine_id, p.name)")
    private List<Parameter> latestParameters;

    public Machine() {
    }

    public Machine(String machineKey, String name) {
        super();
        this.machineKey = machineKey;
        this.name = name;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public Integer getID() {
        return this.id;
    }

    public void setMachineKey(String machineKey) {
        this.machineKey = machineKey;
    }

    public String getMachineKey() {
        return this.machineKey;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setLatestParameters(List<Parameter> latestParameters) {
        this.latestParameters = latestParameters;
    }

    public List<Parameter> getLatestParameters() {
        return this.latestParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Machine m = (Machine) o;
        var isEqual = id == m.id && name.equals(m.name) && machineKey.equals(m.machineKey);
        if (isEqual && this.latestParameters != null) {
            return latestParameters.equals(m.latestParameters);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
