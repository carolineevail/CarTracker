package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

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
                    HashMap mustacheTags = new HashMap<>();
                    String name = request.queryParams("loginName");
                    if (name == null) {
                        return new ModelAndView(mustacheTags, "login.html");
                    }
                    else {
                        Session session = request.session();
                        User user = new User(name);
                        users.put(name, user);
                        mustacheTags.put("user", user);
                        return new ModelAndView(mustacheTags, "home.html");
                    }

                }),
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/create-entry",
                ((request, response) -> {
                    String carMake = request.queryParams("carMake");
                    String carModel = request.queryParams("carModel");
                    int carYear = Integer.valueOf(request.queryParams("carYear"));
                    String carStyle = request.queryParams("carStyle");
                    String carColor = request.queryParams("carColor");
                    Car car = new Car(carMake, carModel, carYear, carStyle, carColor);

                })
        );
//        Spark.post(
//                "/create-entry",
//                ((request, response) -> {
//                    Session session = request.session();
//                    String userName = session.attribute("userName");
//                    if (userName == null) {
//                        throw new Exception("Not logged in.");
//                    }
//
//                    String make = request.queryParams("carMake");
//                    String model = request.queryParams("carModel");
//                    String year = request.queryParams("carYear");
//
//                })
//        )


    }
}
