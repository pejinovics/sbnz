package com.ftn.model;

public class DoorSystem {
    private boolean[] windowsUp;
    private boolean[] doorsClosed;
    private boolean locked;

    public DoorSystem() {
        windowsUp = new boolean[]{true, true, true, true};
        doorsClosed = new boolean[]{true, true, true, true};
        locked = true;
    }
    //TODO nacin dizanja i spustanja prozora
    // nacin na koji ce se postavljati vrednosti, mozda ni ne mora da bude ovako primitivno implementirano
}
