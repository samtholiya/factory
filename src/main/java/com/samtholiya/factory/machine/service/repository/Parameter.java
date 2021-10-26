package com.samtholiya.factory.machine.service.repository;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Parameter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private Machine machine;

    private String name;
    private Float value;

    @CreatedDate
    private LocalDateTime createdDate;

    public Parameter() {
    }

    public Parameter(Machine machine, String name, Float value) {
        this.machine = machine;
        this.name = name;
        this.value = value;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public Integer getID() {
        return this.id;
    }

    public void setMachineID(Machine machine) {
        this.machine = machine;
    }

    public Machine getMachine() {
        return this.machine;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Float getValue() {
        return this.value;
    }

    public void setCreatedDate(LocalDateTime dateCreated) {
        this.createdDate = dateCreated;
    }

    public LocalDateTime getCreatedDate() {
        return this.createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Parameter p = (Parameter) o;
        boolean isEqual = true;
        if (isEqual && id != null)
            isEqual = id.equals(p.id);
        else if (isEqual && p.id != null)
            return false;

        if (isEqual && name != null)
            isEqual = name.equals(p.name);
        else if (isEqual && p.name != null)
            return false;

        if (isEqual && value != null)
            isEqual = value.equals(p.value);
        else if (isEqual && p.value != null)
            return false;

        if (isEqual && this.machine != null)
            isEqual = machine.equals(p.machine);
        else if (isEqual && p.machine != null)
            return false;

        return isEqual;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
