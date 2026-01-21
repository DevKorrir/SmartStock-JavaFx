package com.example.smartstock.controllers;

import com.example.smartstock.dao.SupplierDAO;
import com.example.smartstock.models.Supplier;
import com.example.smartstock.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.sql.SQLException;
import java.util.List;

public class SupplierController {
    private BorderPane mainLayout;
    private SupplierDAO supplierDAO;
    private TableView<Supplier> supplierTable;
    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();
    
    public SupplierController(BorderPane mainLayout, SupplierDAO supplierDAO) {
        this.mainLayout = mainLayout;
        this.supplierDAO = supplierDAO;
    }
    
    public void show() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Suppliers");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        
        Button addBtn = new Button("Add Supplier");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        addBtn.setOnAction(e -> showAddDialog());
        
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        editBtn.setOnAction(e -> editSelected());
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteSelected());
        
        toolbar.getChildren().addAll(addBtn, editBtn, deleteBtn);
        
        supplierTable = new TableView<>();
        supplierTable.setItems(supplierList);
        
        TableColumn<Supplier, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        
        TableColumn<Supplier, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Supplier, String> contactCol = new TableColumn<>("Contact Person");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        contactCol.setPrefWidth(150);
        
        TableColumn<Supplier, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(120);
        
        TableColumn<Supplier, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(180);
        
        supplierTable.getColumns().addAll(idCol, nameCol, contactCol, phoneCol, emailCol);
        
        view.getChildren().addAll(titleLabel, toolbar, supplierTable);
        VBox.setVgrow(supplierTable, Priority.ALWAYS);
        
        mainLayout.setCenter(view);
        loadSuppliers();
    }
    
    private void loadSuppliers() {
        try {
            List<Supplier> suppliers = supplierDAO.findAll();
            supplierList.clear();
            supplierList.addAll(suppliers);
        } catch (SQLException e) {
            AlertUtil.showError("Database Error", "Failed to load suppliers: " + e.getMessage());
        }
    }
    
    private void showAddDialog() {
        Dialog<Supplier> dialog = createFormDialog(null);
        dialog.showAndWait().ifPresent(supplier -> {
            try {
                supplierDAO.save(supplier);
                loadSuppliers();
                AlertUtil.showInfo("Success", "Supplier added successfully!");
            } catch (SQLException e) {
                AlertUtil.showError("Database Error", "Failed to add supplier: " + e.getMessage());
            }
        });
    }
    
    private void editSelected() {
        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a supplier to edit.");
            return;
        }
        
        Dialog<Supplier> dialog = createFormDialog(selected);
        dialog.showAndWait().ifPresent(supplier -> {
            try {
                supplierDAO.update(supplier);
                loadSuppliers();
                AlertUtil.showInfo("Success", "Supplier updated successfully!");
            } catch (SQLException e) {
                AlertUtil.showError("Database Error", "Failed to update supplier: " + e.getMessage());
            }
        });
    }
    
    private void deleteSelected() {
        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a supplier to delete.");
            return;
        }
        
        if (AlertUtil.showConfirmation("Confirm Delete", 
            "Are you sure you want to delete " + selected.getName() + "?")) {
            try {
                supplierDAO.delete(selected.getSupplierId());
                loadSuppliers();
                AlertUtil.showInfo("Success", "Supplier deleted successfully!");
            } catch (SQLException e) {
                AlertUtil.showError("Database Error", "Failed to delete supplier: " + e.getMessage());
            }
        }
    }
    
    private Dialog<Supplier> createFormDialog(Supplier supplier) {
        Dialog<Supplier> dialog = new Dialog<>();
        dialog.setTitle(supplier == null ? "Add Supplier" : "Edit Supplier");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField();
        TextField contactField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextArea addressArea = new TextArea();
        addressArea.setPrefRowCount(3);
        
        if (supplier != null) {
            nameField.setText(supplier.getName());
            contactField.setText(supplier.getContactPerson());
            phoneField.setText(supplier.getPhone());
            emailField.setText(supplier.getEmail());
            addressArea.setText(supplier.getAddress());
        }
        
        grid.add(new Label("Name:*"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Contact Person:"), 0, 1);
        grid.add(contactField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressArea, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Supplier result = supplier != null ? supplier : new Supplier();
                result.setName(nameField.getText().trim());
                result.setContactPerson(contactField.getText().trim());
                result.setPhone(phoneField.getText().trim());
                result.setEmail(emailField.getText().trim());
                result.setAddress(addressArea.getText().trim());
                return result;
            }
            return null;
        });
        
        return dialog;
    }
}