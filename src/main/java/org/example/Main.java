package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import java.util.Properties;

public class Main {
    static String DB_URL;
    static String USER;
    static String PASS;
    static String QUERY = "SELECT Id, Name, Musical_Key FROM Instrument";


    public static void main(String[] args) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("dbconfig.properties")){
            properties.load(fis);
            DB_URL = properties.getProperty("DB_URL");
            USER = properties.getProperty("DB_USER");
            PASS = properties.getProperty("DB_PASS");
        } catch (IOException e){
            e.printStackTrace();
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(QUERY)){
            while (rs.next()){
                System.out.println("Id: " + rs.getInt("Id"));
                System.out.println("Name: " + rs.getString("Name"));
                System.out.println("Musical Key: " + rs.getString("Musical_Key"));

            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}