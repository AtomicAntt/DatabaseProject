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
                        removeMusician(conn, scanner);
                        break;
                    case 3:
                        addAlbum(conn, scanner);
                        break;
                    case 4:
                        removeAlbum(conn, scanner);
                        break;
                    case 5:
                        addSong(conn, scanner);
                        break;
                    case 6:
                        removeSong(conn, scanner);
                        break;
                    case 7:
                        displayInformation(conn);
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
            System.out.println("Musician successfully added!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeMusician(Connection connection, Scanner scanner){
        System.out.println("Enter the musician's SSN to remove them:");
        String ssn = scanner.nextLine();

        String query = "DELETE FROM Musician WHERE SSN = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, ssn);
            statement.executeUpdate();
            System.out.println("Removed the musician with SSN: " + ssn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addAlbum(Connection connection, Scanner scanner){
        System.out.println("Enter album title:");
        String title = scanner.nextLine();
        System.out.println("Enter copyright date (YYYY-MM-DD):");
        String copyrightDate = scanner.nextLine();
        System.out.println("Enter album format:");
        String format = scanner.nextLine();
        System.out.println("Enter album identifier (integer):");
        int albumIdentifier = scanner.nextInt();

        // Insert the album
        String insertQuery = "INSERT INTO Album (Title, CopyrightDate, Format, AlbumIdentifier) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)){
            insertStatement.setString(1, title);
            insertStatement.setDate(2, Date.valueOf(copyrightDate));
            insertStatement.setString(3, format);
            insertStatement.setInt(4, albumIdentifier);
            insertStatement.executeUpdate();
            System.out.println("Album added!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeAlbum(Connection connection, Scanner scanner){
        System.out.println("Enter the ID of the album you want to remove:");
        int id = scanner.nextInt();

        String query = "DELETE FROM Album WHERE AlbumID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Removed the album with ID: " + id );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addSong(Connection connection, Scanner scanner){
        System.out.println("Enter song title:");
        String title = scanner.nextLine();
        System.out.println("Enter song author:");
        String author = scanner.nextLine();

        // Insert the album
        String insertQuery = "INSERT INTO Song (Title, Author) VALUES (?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)){
            insertStatement.setString(1, title);
            insertStatement.setString(2, author);
            insertStatement.executeUpdate();
            System.out.println("Song added!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeSong(Connection connection, Scanner scanner){
        System.out.println("Enter the ID of the song you want to remove:");
        int id = scanner.nextInt();

        String query = "DELETE FROM Song WHERE SongID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Removed the song with ID: " + id );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void displayInformation(Connection connection) throws SQLException {
        String[] tables = {"Home", "Musician", "Instrument", "Album", "Song", "MusicianInstrument", "SongPerformance"};
        for (String table : tables) {
            System.out.println("\nTable: " + table);
            String query = "SELECT * FROM " + table;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Print column headers
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(metaData.getColumnName(i) + "\t|\t");
                }
                System.out.println();

                // Print rows
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(rs.getString(i) + "\t|\t");
                    }
                    System.out.println();
                }
            }
        }
    }
}