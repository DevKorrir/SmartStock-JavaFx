package com.example.smartstock;

import com.example.smartstock.controllers.MainController;
import com.example.smartstock.db.DatabaseConnection;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.sql.SQLException;

public class MainApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Test database connection
            DatabaseConnection.getInstance().testConnection();
            
            // Initialize main controller
            MainController mainController = new MainController(primaryStage);
            mainController.initialize();
            
        } catch (SQLException e) {
            showDatabaseError(e);
        } catch (Exception e) {
            showGeneralError(e);
        }
    }
    
    private void showDatabaseError(SQLException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Connection Error");
        alert.setHeaderText("Failed to connect to the database");
        alert.setContentText("Please check your db.properties file and ensure MariaDB is running.\n\n" +
                           "Error: " + e.getMessage());
        alert.showAndWait();
        System.exit(1);
    }
    
    private void showGeneralError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Application Error");
        alert.setHeaderText("An error occurred while starting the application");
        alert.setContentText("Error: " + e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
        System.exit(1);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}