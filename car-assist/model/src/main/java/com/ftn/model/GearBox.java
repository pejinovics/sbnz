package com.ftn.model;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class GearBox {
    public enum State {
        UP, DOWN, OK
    };
    private Integer currentGear;
    private Integer currentRPM;
    private  State state;

    public GearBox(Integer currentGear, Integer currentRPM, State state) {
        this.currentGear = currentGear;
        this.currentRPM = currentRPM;
        this.state = state;
    }

    public Integer getCurrentGear() {
        return currentGear;
    }

    public void setCurrentGear(Integer currentGear) {
        this.currentGear = currentGear;
    }

    public Integer getCurrentRPM() {
        return currentRPM;
    }

    public void setCurrentRPM(Integer currentRPM) {
        this.currentRPM = currentRPM;
    }

    public void setCurrentRPM(double currentRPM) { this.currentRPM = (int) currentRPM; }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
