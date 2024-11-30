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

    public static void main(String[] args) {
        // Retrieve values for variables containing DB information from the dbconfig.properties file.
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("dbconfig.properties")){
            properties.load(fis);
            DB_URL = properties.getProperty("DB_URL");
            USER = properties.getProperty("DB_USER");
            PASS = properties.getProperty("DB_PASS");
        } catch (IOException e){
            e.printStackTrace();
        }

        // Take user input so that they can run commands
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)){
            System.out.println("Successfully connected to database " + USER + "!");

            while (true){
                System.out.println("\nSelect an option (1-7)");
                System.out.println("1. Add a musician");
                System.out.println("2. Remove a musician");
                System.out.println("3. Add an album");
                System.out.println("4. Remove an album");
                System.out.println("5. Add a song");
                System.out.println("6. Remove a song");
                System.out.println("7. Display the information of all tables in the database\n");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        addMusician(conn, scanner);
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    default:
                        System.out.println("Invalid choice");
                        break;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void addMusician(Connection connection, Scanner scanner){
        System.out.println("Enter musician's SSN:");
        String ssn = scanner.nextLine();
        System.out.println("Enter musician's name:");
        String name = scanner.nextLine();
        System.out.println("Enter musician's home address:");
        String address = scanner.nextLine();
        System.out.println("Enter musician's phone number:");
        String phoneNum = scanner.nextLine();

        // Find out if the home address given exists

        String findHomeQuery = "SELECT Address FROM Home WHERE Address = ?";

        try (PreparedStatement findHomeStatement = connection.prepareStatement(findHomeQuery)){
            findHomeStatement.setString(1, address);
            ResultSet resultSet =  findHomeStatement.executeQuery();
            if (!resultSet.next()){
                // Home address is not found, so it is invalid
                System.out.println("ERROR: Home address given does not exist.");
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Home address found, insert musician with user given values
        String insertMusicianQuery = "INSERT INTO Musician (SSN, Name, Phone, Address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertMusicianStatement = connection.prepareStatement(insertMusicianQuery)){
            insertMusicianStatement.setString(1, ssn);
            insertMusicianStatement.setString(2, name);
            insertMusicianStatement.setString(3, phoneNum);
            insertMusicianStatement.setString(4, address);
            insertMusicianStatement.executeUpdate();
            System.out.println("Musician with SSN " + ssn + ", name " + name + ", phone # " + phoneNum + ", and lives at " + address + " has been added to the database!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}