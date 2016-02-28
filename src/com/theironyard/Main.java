package com.theironyard;

import spark.Session;
import spark.Spark;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> users = new HashMap<>();
    static ArrayList<Car> cars = new ArrayList<>();

    public static void main(String[] args) {

        Spark.init();

        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("userName");
                    if (userName == null) {
                        users.put(userName, new User(userName));
                    }
                    else {
                        users.containsKey().
                    }
                })
        )
    }
}
