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
public class CurrentSpeedEvent implements Serializable {
    private static final long serialVersionID = 1L;
    private final double currentSpeed;
    private final Date timestamp;
    private boolean carInFront;
    public CurrentSpeedEvent(double currentSpeed, Date timestamp) {
        this.currentSpeed = currentSpeed;
        this.timestamp = timestamp;
    }
    public CurrentSpeedEvent(double currentSpeed, Date timestamp, boolean carInFront) {
        this(currentSpeed, timestamp);
        this.carInFront = carInFront;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public boolean isCarInFront() {
        return carInFront;
    }
}
