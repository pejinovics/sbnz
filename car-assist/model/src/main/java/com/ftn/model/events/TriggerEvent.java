package com.ftn.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import java.util.Date;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("3m")
public class TriggerEvent {
    private final TriggerType type;
    private Date timestamp;
    public TriggerEvent(TriggerType type) {
        this(type, new Date());
    }
    public TriggerEvent(TriggerType type, Date timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }
    public TriggerType getType() {
        return type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
