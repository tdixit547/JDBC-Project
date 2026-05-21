package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static Connection connection = null;
    private static final String CONFIG_FILE = "resources/db.properties";

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                props.load(fis);
            } catch (IOException e) {
                throw new SQLException("Cannot load database configuration from " + CONFIG_FILE + ". " +
                        "Please ensure the file exists with keys: db.url, db.user, db.password, db.driver", e);
            }

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            String driver = props.getProperty("db.driver");

            if (url == null || user == null || password == null) {
                throw new SQLException("Missing required database properties (db.url, db.user, db.password) in " + CONFIG_FILE);
            }

            try {
                if (driver != null) {
                    Class.forName(driver);
                }
            } catch (ClassNotFoundException e) {
                throw new SQLException("JDBC Driver not found: " + driver + 
                        ". Ensure mysql-connector-j JAR is in the classpath.", e);
            }

            System.out.println("[DBConnection] Connecting to database...");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("[DBConnection] Connected successfully.");
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("[DBConnection] Connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("[DBConnection] Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
