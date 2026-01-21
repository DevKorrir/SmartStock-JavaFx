module com.example.smartstock {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.mariadb.jdbc;
    requires org.controlsfx.controls;

    opens com.example.smartstock to javafx.fxml;
    opens com.example.smartstock.controllers to javafx.fxml;
    opens com.example.smartstock.models to javafx.base;

    exports com.example.smartstock;
    exports com.example.smartstock.controllers;
    exports com.example.smartstock.dao;
    exports com.example.smartstock.models;
    exports com.example.smartstock.util;
    exports com.example.smartstock.db;
}