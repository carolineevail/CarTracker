package com.theironyard;

/**
 * Created by Caroline on 2/26/16.
 */
public class Car {
    String make;
    String model;
    int modelYear;
    String style;
    String color;

    public Car(String make, String model, int modelYear, String style, String color) {
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
        this.style = style;
        this.color = color;
    }

    @Override
    public String toString() {
        return "Car{" +
                "make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", modelYear=" + modelYear +
                ", style='" + style + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
