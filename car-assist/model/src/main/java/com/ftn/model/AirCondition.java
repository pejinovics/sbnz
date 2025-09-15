package com.ftn.model;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class AirCondition {

    public enum State {
        COOL,
        HEAT,
        OFF
    }

    private State state;
    private Double desiredTemp;
    private Double measuredTemp;

    public AirCondition() {

    }

    public AirCondition(State state, Double desiredTemp, Double measuredTemp) {
        this.state = state;
        this.desiredTemp = desiredTemp;
        this.measuredTemp = measuredTemp;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Double getDesiredTemp() {
        return desiredTemp;
    }

    public void setDesiredTemp(Double desiredTemp) {
        this.desiredTemp = desiredTemp;
    }

    public Double getMeasuredTemp() {
        return measuredTemp;
    }

    public void setMeasuredTemp(Double measuredTemp) {
        this.measuredTemp = measuredTemp;
    }
}
