package com.theironyard;


import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.Assert.*;


/**
 * Created by Caroline on 3/1/16.
 */
public class MainTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./test");
        Main.createTables(conn);
        return conn;
    }

    public void endConnection(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE users");
        stmt.execute("DROP TABLE cars");
        conn.close();
    }

    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice");
        User user = Main.selectUser(conn, "Alice");
        endConnection(conn);
        assertTrue(user != null);

    }

    @Test
    public void testEntry() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice");
        User user = Main.selectUser(conn, "Alice");
        Main.insertEntry(conn, "", 1, "honda", "accord", 2345, "wagon", "blue");
        Car car = Main.selectEntry(conn, "");
        endConnection(conn);
        assertTrue(car != null);
    }

    @Test
    public void testEntries() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice");
        Main.insertUser(conn, "Bob");
        Main.insertEntry(conn, "", 1, "honda", "accord", 2007, "sedan", "white");
        Main.insertEntry(conn, "", 2, "honda", "civic", 2014, "coupe", "blue");
        ArrayList<Car> cars = Main.selectEntries(conn);
        endConnection(conn);
        assertTrue(cars.size()==2);

    }

}

//stmt.setInt(1, userId);
//        stmt.setString(2, make);
//        stmt.setString(3, model);
//        stmt.setInt(4, modelYear);
//        stmt.setString(5, style);
//        stmt.setString(6, color);
