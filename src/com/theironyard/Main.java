package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.*;

public class Main {

    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {

        Spark.init();
        Spark.get(
                "/",
                ((request, response) -> {
                    User user = getUserFromSession(request.session());
                    HashMap mustacheTags = new HashMap<>();

                    if (user == null) {
                        Collection<User> currentUsers = users.values();
                        ArrayList<Car> cars = new ArrayList<Car>();
                        for (User person : currentUsers) {
                            cars.addAll(person.cars);
                        }
                        mustacheTags.put("cars", cars);
                        return new ModelAndView(mustacheTags, "login.html");
                    } else if (request.session().attribute("id") != null) {
                        for (int i = 0; i < user.cars.size(); i++) {
                            if (request.session().attribute("id").equals(user.cars.get(i).id)) {
                                request.session().removeAttribute("id");
                                mustacheTags.put("car", user.cars.get(i));
                            }
                        }
                        return new ModelAndView(mustacheTags, "edit.html");
                    } else {
                        mustacheTags.put("user", user);
                        mustacheTags.put("cars", user.cars);
                        return new ModelAndView(mustacheTags, "home.html");
                    }

                }),
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/create-user",
                ((request, response) -> {
                    String name = request.queryParams("loginName");
                    if (name == null) {
                        throw new Exception("Login name is null.");
                    }
                    User user = new User(name);
                    if (!users.containsKey(name)) {
                        user = new User(name);
                        users.put(name, user);
                    }
                    Session session = request.session();
                    session.attribute("user", user);
                    response.redirect("/");

                    return "";
                })
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
                    String id  = UUID.randomUUID().toString();
                    Car car = new Car(carMake, carModel, carYear, carStyle, carColor, id);
                    User user = getUserFromSession(request.session());
                    user.cars.add(car);
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/delete-entry",
                ((request, response) -> {
                    User user = getUserFromSession(request.session());
                    for (int i = 0; i < user.cars.size(); i++) {
                        if (request.queryParams("id").equals(user.cars.get(i).id)) {
                            user.cars.remove(i);
                        }
                    }
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/edit-entry",
                ((request, response) -> {
                    User user = getUserFromSession(request.session());
                    for (int i = 0; i < user.cars.size(); i++) {
                        if (request.queryParams("id").equals(user.cars.get(i).id)) {
                            request.session().attribute("id", user.cars.get(i).id);
                        }
                    }
                    response.redirect("/");
                    return user;
                })
        );
        Spark.post(
                "/perform-update",
                ((request, response) -> {
                    String id = request.queryParams("id");
                    String carMake = request.queryParams("carMake");
                    String carModel = request.queryParams("carModel");
                    int carYear = Integer.valueOf(request.queryParams("carYear"));
                    String carStyle = request.queryParams("carStyle");
                    String carColor = request.queryParams("carColor");
                    Car car = new Car(carMake, carModel, carYear, carStyle, carColor, id);
                    User user = getUserFromSession(request.session());
                    for (int i = 0; i < user.cars.size(); i++) {
                        if (request.queryParams("id").equals(user.cars.get(i).id)) {
                            user.cars.remove(i);
                            user.cars.add(i, car);
                        }
                    }
                    response.redirect("/");
                    return "";
                })
        );


    }
    static User getUserFromSession(Session session) {
        return session.attribute("user");
    }
}
