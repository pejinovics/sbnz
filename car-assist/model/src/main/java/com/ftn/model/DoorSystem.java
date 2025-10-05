package com.ftn.model;

public class DoorSystem {
    private boolean[] windowsClosed;
    private boolean[] doorsClosed;
    private boolean keyPressed;

    public DoorSystem() {
        doorsClosed = new boolean[]{true, true, true, true};        // front left, front right, rear left, rear right
        windowsClosed = new boolean[]{true, true, true, true};
        keyPressed = false;
    }

    public boolean isKeyPressed() {
        return keyPressed;
    }

    public void setKeyPressed(boolean keyPressed) {
        this.keyPressed = keyPressed;
    }

    public void setDoor(Side side, boolean closed) {
        int index = side.ordinal();
        doorsClosed[index] = closed;
    }
    public void setWindow(Side side, boolean up) {
        int index = side.ordinal();
        windowsClosed[index] = up;
    }
    public int getOpenDoorIndex() {
        for (int index = 0; index < doorsClosed.length; index++) {
            if (!doorsClosed[index]) return index;
        }
        return -1;
    }
    public int getOpenWindowIndex() {
        for (int index = 0; index < windowsClosed.length; index++) {
            if(!windowsClosed[index]) return index;
        }
        return -1;
    }

}
