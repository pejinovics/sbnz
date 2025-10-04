package com.ftn.model.events;

public class TriggerEvent {
    private boolean processed;
    private final TriggerType type;

    public TriggerEvent(TriggerType type) {
        this.type = type;
        this.processed = false;
    }
    public void markAsProcessed() {
        this.processed = true;
    }
    public TriggerType getType() {
        return type;
    }
}
