package com.example.smartstock.controllers;

import com.example.smartstock.dao.*;
import com.example.smartstock.models.*;
import com.example.smartstock.util.ValidationUtil;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductFormDialog extends Dialog<Product> {
    private TextField nameField;
    private TextArea descField;
    private TextField skuField;
    private ComboBox<Category> categoryCombo;
    private ComboBox<Supplier> supplierCombo;
    private TextField priceField;
    private TextField stockField;
    private TextField minStockField;
    
    private Product product;
    
    public ProductFormDialog(Stage owner, Product product, CategoryDAO categoryDAO, SupplierDAO supplierDAO) {
        this.product = product;
        
        setTitle(product == null ? "Add Product" : "Edit Product");
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        nameField = new TextField();
        descField = new TextArea();
        descField.setPrefRowCount(3);
        skuField = new TextField();
        categoryCombo = new ComboBox<>();
        supplierCombo = new ComboBox<>();
        priceField = new TextField();
        stockField = new TextField();
        minStockField = new TextField();
        
        // Load categories and suppliers
        try {
            List<Category> categories = categoryDAO.findAll();
            categoryCombo.getItems().addAll(categories);
            
            List<Supplier> suppliers = supplierDAO.findAll();
            supplierCombo.getItems().addAll(suppliers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        if (product != null) {
            nameField.setText(product.getName());
            descField.setText(product.getDescription());
            skuField.setText(product.getSku());
            priceField.setText(product.getUnitPrice().toString());
            stockField.setText(product.getCurrentStock().toString());
            minStockField.setText(product.getMinimumStock().toString());
            
            if (product.getCategoryId() != null) {
                categoryCombo.getItems().stream()
                    .filter(c -> c.getCategoryId().equals(product.getCategoryId()))
                    .findFirst()
                    .ifPresent(categoryCombo::setValue);
            }
            
            if (product.getSupplierId() != null) {
                supplierCombo.getItems().stream()
                    .filter(s -> s.getSupplierId().equals(product.getSupplierId()))
                    .findFirst()
                    .ifPresent(supplierCombo::setValue);
            }
        }
        
        grid.add(new Label("Name:*"), 0, 0);
        grid.add(nameField, 1, 0);
        
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        
        grid.add(new Label("SKU:"), 0, 2);
        grid.add(skuField, 1, 2);
        
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryCombo, 1, 3);
        
        grid.add(new Label("Supplier:"), 0, 4);
        grid.add(supplierCombo, 1, 4);
        
        grid.add(new Label("Price:*"), 0, 5);
        grid.add(priceField, 1, 5);
        
        grid.add(new Label("Current Stock:*"), 0, 6);
        grid.add(stockField, 1, 6);
        
        grid.add(new Label("Minimum Stock:*"), 0, 7);
        grid.add(minStockField, 1, 7);
        
        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateInput()) {
                event.consume();
            }
        });
        
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Product result = this.product != null ? this.product : new Product();
                result.setName(nameField.getText().trim());
                result.setDescription(descField.getText().trim());
                result.setSku(skuField.getText().trim());
                result.setCategoryId(categoryCombo.getValue() != null ? 
                    categoryCombo.getValue().getCategoryId() : null);
                result.setSupplierId(supplierCombo.getValue() != null ? 
                    supplierCombo.getValue().getSupplierId() : null);
                result.setUnitPrice(new BigDecimal(priceField.getText().trim()));
                result.setCurrentStock(Integer.parseInt(stockField.getText().trim()));
                result.setMinimumStock(Integer.parseInt(minStockField.getText().trim()));
                return result;
            }
            return null;
        });
    }
    
    private boolean validateInput() {
        if (ValidationUtil.isNullOrEmpty(nameField.getText())) {
            showError("Name is required");
            return false;
        }
        
        if (!ValidationUtil.isValidDecimal(priceField.getText())) {
            showError("Invalid price");
            return false;
        }
        
        if (!ValidationUtil.isValidNumber(stockField.getText())) {
            showError("Invalid stock quantity");
            return false;
        }
        
        if (!ValidationUtil.isValidNumber(minStockField.getText())) {
            showError("Invalid minimum stock");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}