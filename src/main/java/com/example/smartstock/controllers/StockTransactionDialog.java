package com.example.smartstock.controllers;

import com.example.smartstock.models.Product;
import com.example.smartstock.models.StockTransaction;
import com.example.smartstock.models.StockTransaction.TransactionType;
import com.example.smartstock.util.ValidationUtil;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StockTransactionDialog extends Dialog<StockTransaction> {
    private ComboBox<TransactionType> typeCombo;
    private TextField quantityField;
    private TextField referenceField;
    private TextArea notesArea;
    
    private Product product;
    
    public StockTransactionDialog(Stage owner, Product product) {
        this.product = product;
        
        setTitle("Stock Transaction - " + product.getName());
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        Label productLabel = new Label(product.getName());
        productLabel.setStyle("-fx-font-weight: bold;");
        
        Label currentStockLabel = new Label("Current Stock: " + product.getCurrentStock());
        
        typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(TransactionType.values());
        typeCombo.setValue(TransactionType.IN);
        
        quantityField = new TextField();
        referenceField = new TextField();
        notesArea = new TextArea();
        notesArea.setPrefRowCount(3);
        
        grid.add(new Label("Product:"), 0, 0);
        grid.add(productLabel, 1, 0);
        
        grid.add(new Label("Current Stock:"), 0, 1);
        grid.add(currentStockLabel, 1, 1);
        
        grid.add(new Label("Type:*"), 0, 2);
        grid.add(typeCombo, 1, 2);
        
        grid.add(new Label("Quantity:*"), 0, 3);
        grid.add(quantityField, 1, 3);
        
        grid.add(new Label("Reference:"), 0, 4);
        grid.add(referenceField, 1, 4);
        
        grid.add(new Label("Notes:"), 0, 5);
        grid.add(notesArea, 1, 5);
        
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
                StockTransaction transaction = new StockTransaction();
                transaction.setProductId(product.getProductId());
                transaction.setTransactionType(typeCombo.getValue());
                transaction.setQuantity(Integer.parseInt(quantityField.getText().trim()));
                transaction.setReference(referenceField.getText().trim());
                transaction.setNotes(notesArea.getText().trim());
                return transaction;
            }
            return null;
        });
    }
    
    private boolean validateInput() {
        if (!ValidationUtil.isValidNumber(quantityField.getText())) {
            showError("Invalid quantity");
            return false;
        }
        
        int quantity = Integer.parseInt(quantityField.getText().trim());
        if (quantity <= 0) {
            showError("Quantity must be greater than 0");
            return false;
        }
        
        if (typeCombo.getValue() == TransactionType.OUT && quantity > product.getCurrentStock()) {
            showError("Insufficient stock. Available: " + product.getCurrentStock());
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