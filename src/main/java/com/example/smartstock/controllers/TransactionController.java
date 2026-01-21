package com.example.smartstock.controllers;

import com.example.smartstock.dao.StockTransactionDAO;
import com.example.smartstock.models.StockTransaction;
import com.example.smartstock.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionController {
    private BorderPane mainLayout;
    private StockTransactionDAO transactionDAO;
    private TableView<StockTransaction> transactionTable;
    private ObservableList<StockTransaction> transactionList = FXCollections.observableArrayList();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public TransactionController(BorderPane mainLayout, StockTransactionDAO transactionDAO) {
        this.mainLayout = mainLayout;
        this.transactionDAO = transactionDAO;
    }
    
    public void show() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Stock Transactions");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        transactionTable = new TableView<>();
        transactionTable.setItems(transactionList);
        
        TableColumn<StockTransaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        idCol.setPrefWidth(60);
        
        TableColumn<StockTransaction, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productCol.setPrefWidth(250);
        
        TableColumn<StockTransaction, StockTransaction.TransactionType> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        typeCol.setPrefWidth(80);
        typeCol.setCellFactory(col -> new TableCell<StockTransaction, StockTransaction.TransactionType>() {
            @Override
            protected void updateItem(StockTransaction.TransactionType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.name());
                    if (item == StockTransaction.TransactionType.IN) {
                        setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                    } else {
                        setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    }
                }
            }
        });
        
        TableColumn<StockTransaction, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(100);
        
        TableColumn<StockTransaction, LocalDateTime> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        dateCol.setPrefWidth(150);
        dateCol.setCellFactory(col -> new TableCell<StockTransaction, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormatter.format(item));
                }
            }
        });
        
        TableColumn<StockTransaction, String> refCol = new TableColumn<>("Reference");
        refCol.setCellValueFactory(new PropertyValueFactory<>("reference"));
        refCol.setPrefWidth(150);
        
        TableColumn<StockTransaction, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));
        notesCol.setPrefWidth(250);
        
        transactionTable.getColumns().addAll(idCol, productCol, typeCol, quantityCol, dateCol, refCol, notesCol);
        
        view.getChildren().addAll(titleLabel, transactionTable);
        VBox.setVgrow(transactionTable, Priority.ALWAYS);
        
        mainLayout.setCenter(view);
        loadTransactions();
    }
    
    private void loadTransactions() {
        try {
            List<StockTransaction> transactions = transactionDAO.findAll();
            transactionList.clear();
            transactionList.addAll(transactions);
        } catch (SQLException e) {
            AlertUtil.showError("Database Error", "Failed to load transactions: " + e.getMessage());
        }
    }
}