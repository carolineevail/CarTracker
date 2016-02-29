package com.theironyard;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Caroline on 2/26/16.
 */
public class User {
    String name;
    int id;
    ArrayList<Car> cars = new ArrayList<Car>();


    public User(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public User(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}
