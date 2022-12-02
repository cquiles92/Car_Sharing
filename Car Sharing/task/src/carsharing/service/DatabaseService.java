package carsharing.service;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Class designed to handle and serve connections to database.
 */
public class DatabaseService {
    private static final String DRIVER = "org.h2.Driver";
    private static String URL;
//    private static final String USER = "";
//    private static final String PASSWORD = "";

    public DatabaseService(String URL) {
        DatabaseService.URL = URL;
    }


    public Connection getConnection() {
        try {
            Class.forName(DRIVER);
            Connection connection = DriverManager.getConnection(URL);
            connection.setAutoCommit(true);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
