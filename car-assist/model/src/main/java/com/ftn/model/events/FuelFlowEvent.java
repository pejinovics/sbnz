package com.ftn.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("10m")
public class FuelFlowEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private double fuelMiligrams;
    private Date timestamp;

    public FuelFlowEvent(double fuelMiligrams, Date timestamp) {
        this.fuelMiligrams = fuelMiligrams;
        this.timestamp = timestamp;
    }
    public double getFuelMiligrams() {
        return fuelMiligrams;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setFuelMiligrams(double fuelMiligrams) {
        this.fuelMiligrams = fuelMiligrams;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
