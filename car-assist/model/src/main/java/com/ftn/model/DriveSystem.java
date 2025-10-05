package com.ftn.model;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class DriveSystem {
    private double steeringAngle;
    private boolean brakePressed;
    private boolean isLeftTurnSignal;
    private boolean isRightTurnSignal;

    public DriveSystem(double steeringAngle, boolean brakePressed, boolean isLeftTurnSignal, boolean isRightTurnSignal) {
        this.steeringAngle = steeringAngle;
        this.brakePressed = brakePressed;
        this.isLeftTurnSignal = isLeftTurnSignal;
        this.isRightTurnSignal = isRightTurnSignal;
    }

    public double getSteeringAngle() {
        return steeringAngle;
    }

    public void setSteeringAngle(double steeringAngle) {
        this.steeringAngle = steeringAngle;
    }

    public boolean isBrakePressed() {
        return brakePressed;
    }

    public void setBrakePressed(boolean brakePressed) {
        this.brakePressed = brakePressed;
    }

    public boolean isLeftTurnSignal() {
        return isLeftTurnSignal;
    }

    public void setLeftTurnSignal(boolean leftTurnSignal) {
        isLeftTurnSignal = leftTurnSignal;
    }

    public boolean isRightTurnSignal() {
        return isRightTurnSignal;
    }

    public void setRightTurnSignal(boolean rightTurnSignal) {
        isRightTurnSignal = rightTurnSignal;
    }
}
