package com.ftn.model;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Tyre {

    public enum Season {
        WINTER,
        SUMMER,
        ALLSEASON
    }

    public enum Side {
        FRONT,
        REAR
    }

//    public enum Side {}

    private Double preassure;

    private Season season;

    private Side side;

    public Tyre(Double pressure, Season season, Side side) {
        this.preassure = pressure;
        this.season = season;
        this.side = side;
    }

    public Season getSeason() {
        return season;
    }

    public Side getSide() {
        return side;
    }

    public Double getPreassure() {
        return preassure;
    }

    public void setPreassure(Double preassure) {
        this.preassure = preassure;
    }
}
