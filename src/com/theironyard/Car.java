package com.theironyard;

/**
 * Created by Caroline on 2/26/16.
 */
public class Car {
    String name;
    String id;
    int userId;
    String make;
    String model;
    int modelYear;
    String style;
    String color;


    public Car(String id, int userId, String make, String model, int modelYear, String style, String color) {
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
        this.style = style;
        this.color = color;
        this.id = id;
        this.userId = userId;
    }

    public Car(String name, String make, String model, int modelYear, String color, String style) {
        this.name = name;
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
        this.color = color;
        this.style = style;
    }

    public Car(String make, String model, int modelYear, String style, String color, String id) {
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
        this.style = style;
        this.color = color;
        this.id = id;
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
