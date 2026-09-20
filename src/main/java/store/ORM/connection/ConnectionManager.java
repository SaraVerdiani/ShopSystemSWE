package store.ORM.connection;

import java.sql.*;

public class ConnectionManager {

    private static ConnectionManager instance;
    private Connection connection;

    private ConnectionManager() {
        try {
            this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ShopSystemDB","postgres","1234");
        } catch (SQLException e){
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    public static ConnectionManager getInstance() {
        if(instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ShopSystemDB","postgres","1234");
            }
        } catch(SQLException e) {
            System.err.printf("Can't get connection: " + e.getMessage());
        }
        return connection;
    }


}
