package com.ftn.model;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class SurroundSystem {
    private double leftLineDistance;
    private double rightLineDistance;
    private boolean lineVisible;
    private double frontVehicleDistance;
    private double yawRate;

    public SurroundSystem(double leftLineDistance, double rightLineDistance, boolean lineVisible, double frontVehicleDistance, double yawRate) {
        this.leftLineDistance = leftLineDistance;
        this.rightLineDistance = rightLineDistance;
        this.lineVisible = lineVisible;
        this.frontVehicleDistance = frontVehicleDistance;
        this.yawRate = yawRate;
    }

    public double getLeftLineDistance() {
        return leftLineDistance;
    }

    public void setLeftLineDistance(double leftLineDistance) {
        this.leftLineDistance = leftLineDistance;
    }

    public double getRightLineDistance() {
        return rightLineDistance;
    }

    public void setRightLineDistance(double rightLineDistance) {
        this.rightLineDistance = rightLineDistance;
    }

    public boolean isLineVisible() {
        return lineVisible;
    }

    public void setLineVisible(boolean lineVisible) {
        this.lineVisible = lineVisible;
    }

    public double getFrontVehicleDistance() {
        return frontVehicleDistance;
    }

    public void setFrontVehicleDistance(double frontVehicleDistance) {
        this.frontVehicleDistance = frontVehicleDistance;
    }

    public double getYawRate() {
        return yawRate;
    }

    public void setYawRate(double yawRate) {
        this.yawRate = yawRate;
    }
}
