package com.example.smartstock.controllers;

import com.example.smartstock.dao.ProductDAO;
import com.example.smartstock.models.Product;
import com.example.smartstock.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class LowStockController {
    private BorderPane mainLayout;
    private ProductDAO productDAO;
    private TableView<Product> lowStockTable;
    private ObservableList<Product> lowStockList = FXCollections.observableArrayList();
    
    public LowStockController(BorderPane mainLayout, ProductDAO productDAO) {
        this.mainLayout = mainLayout;
        this.productDAO = productDAO;
    }
    
    public void show() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Low Stock Alert");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        
        Label infoLabel = new Label("Products that need restocking:");
        infoLabel.setStyle("-fx-font-size: 14px;");
        
        lowStockTable = new TableView<>();
        lowStockTable.setItems(lowStockList);
        
        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("productId"));
        idCol.setPrefWidth(60);
        
        TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(250);
        
        TableColumn<Product, String> skuCol = new TableColumn<>("SKU");
        skuCol.setCellValueFactory(new PropertyValueFactory<>("sku"));
        skuCol.setPrefWidth(120);
        
        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        categoryCol.setPrefWidth(150);
        
        TableColumn<Product, String> supplierCol = new TableColumn<>("Supplier");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        supplierCol.setPrefWidth(180);
        
        TableColumn<Product, Integer> currentCol = new TableColumn<>("Current Stock");
        currentCol.setCellValueFactory(new PropertyValueFactory<>("currentStock"));
        currentCol.setPrefWidth(120);
        currentCol.setCellFactory(col -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                }
            }
        });
        
        TableColumn<Product, Integer> minCol = new TableColumn<>("Min Stock");
        minCol.setCellValueFactory(new PropertyValueFactory<>("minimumStock"));
        minCol.setPrefWidth(100);
        
        TableColumn<Product, Integer> neededCol = new TableColumn<>("Needed");
        neededCol.setCellValueFactory(new PropertyValueFactory<>("minimumStock"));
        neededCol.setPrefWidth(100);
        neededCol.setCellFactory(col -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Product product = getTableView().getItems().get(getIndex());
                    int needed = product.getMinimumStock() - product.getCurrentStock();
                    setText(String.valueOf(Math.max(0, needed)));
                }
            }
        });
        
        lowStockTable.getColumns().addAll(idCol, nameCol, skuCol, categoryCol, supplierCol, 
                                          currentCol, minCol, neededCol);
        
        view.getChildren().addAll(titleLabel, infoLabel, lowStockTable);
        VBox.setVgrow(lowStockTable, Priority.ALWAYS);
        
        mainLayout.setCenter(view);
        loadLowStockProducts();
    }
    
    private void loadLowStockProducts() {
        try {
            List<Product> products = productDAO.findLowStock();
            lowStockList.clear();
            lowStockList.addAll(products);
            
            if (products.isEmpty()) {
                AlertUtil.showInfo("Low Stock", "No products are currently low on stock!");
            }
        } catch (SQLException e) {
            AlertUtil.showError("Database Error", "Failed to load low stock products: " + e.getMessage());
        }
    }
}