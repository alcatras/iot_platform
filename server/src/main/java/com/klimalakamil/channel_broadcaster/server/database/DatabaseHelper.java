package com.klimalakamil.channel_broadcaster.server.database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 15.01.17.
 */
public class DatabaseHelper {

    private Logger logger;

    private Connection connection;
    private String version;

    public DatabaseHelper(String url, String user, String password) {
        logger = Logger.getLogger(DatabaseHelper.class.getName());
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);

            ResultSet versionResultSet = executeQueryForResult("SELECT VERSION()");
            if (versionResultSet.next()) {
                version = versionResultSet.getString(1);
                versionResultSet.close();
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public ResultSet executeQueryForResult(String sql) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public void executeQuery(String sql) {
        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public boolean checkIfTableExists(String tableName) {
        ResultSet tableResultSet = executeQueryForResult("SELECT COUNT(*) " +
                "FROM information_schema.tables " +
                "WHERE table_name = '" + tableName + "';");

        try {
            return tableResultSet.next() && tableResultSet.getInt(1) > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    public void shutdownConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}