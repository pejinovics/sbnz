package com.ftn.model;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Tyre {

//    public static enum Season {
//        WINTER,
//        SUMMER,
//        ALLSEASON
//    }
//
//    public static enum Side {
//        FRONT_LEFT,
//        FRONT_RIGHT,
//        REAR_LEFT,
//        REAR_RIGHT
//    }

//    public enum Side {}

    private Double pressure;

    private TyreSeason season;

    private TyreSide side;

    public Tyre(Double pressure, TyreSeason season, TyreSide side) {
        this.pressure = pressure;
        this.season = season;
        this.side = side;
    }

    public TyreSeason getSeason() {
        return season;
    }

    public TyreSide getSide() {
        return side;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }
}
