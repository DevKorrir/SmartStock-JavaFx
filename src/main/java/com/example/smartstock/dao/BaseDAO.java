package com.example.smartstock.dao;

import com.example.smartstock.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDAO {
    
    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
    
    protected void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
