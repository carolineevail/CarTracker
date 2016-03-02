package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.*;


public class Main {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS cars (id UUID, user_id INT, make VARCHAR, model VARCHAR, model_year INT, style VARCHAR, color VARCHAR)");
    }

    public static void insertUser (Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?)");
        stmt.setString(1, name);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            return new User(name, id);
        }
        return null;
    }

    public static void insertEntry(Connection conn, String id, int userId, String make, String model, int modelYear, String style, String color) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO cars VALUES (NULL, ?, ?, ?, ?, ?, ?)");
        stmt.setInt(1, userId);
        stmt.setString(2, make);
        stmt.setString(3, model);
        stmt.setInt(4, modelYear);
        stmt.setString(5, style);
        stmt.setString(6, color);
        stmt.execute();
    }

    public static Car selectEntry(Connection conn, String id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement( "SELECT * " +
                                                        "FROM cars " +
                                                        "JOIN users " +
                                                        "ON cars.user_id = users.id " +
                                                        "WHERE cars.id = ?");
        stmt.setString(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String name = results.getString("users.name");
            String make = results.getString("cars.make");
            String model = results.getString("cars.model");
            int modelYear = results.getInt("cars.model_year");
            String style = results.getString("cars.style");
            String color = results.getString("cars.color");
            return new Car(name, make, model, modelYear, style, color);
        }
        return null;
    }

    public static ArrayList<Car> selectEntries(Connection conn) throws SQLException {
        ArrayList<Car> cars = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cars INNER JOIN users ON cars.user_id = users.id WHERE cars.");
        ResultSet results = stmt.executeQuery();
        while (results.next()); {
            String name = results.getString("users.name");
            String make = results.getString("cars.make");
            String model = results.getString("cars.model");
            int modelYear = results.getInt("cars.model_year");
            String style = results.getString("cars.style");
            String color = results.getString("cars.color");
            Car car = new Car(name, make, model, modelYear, style, color);
            cars.add(car);
        }
        return cars;
    }



    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);


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
