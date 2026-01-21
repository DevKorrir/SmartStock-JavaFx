package com.example.smartstock.controllers;

import com.example.smartstock.dao.*;
import com.example.smartstock.models.*;
import com.example.smartstock.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import java.sql.SQLException;
import java.util.List;

public class MainController {
    private final Stage primaryStage;
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final StockTransactionDAO transactionDAO = new StockTransactionDAO();
    
    private BorderPane mainLayout;
    private TableView<Product> productTable;
    private ObservableList<Product> productList = FXCollections.observableArrayList();
    
    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public void initialize() {
        mainLayout = new BorderPane();
        mainLayout.setTop(createTopBar());
        mainLayout.setLeft(createSidebar());
        mainLayout.setCenter(createProductView());
        
        Scene scene = new Scene(mainLayout, 1400, 800);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("SmartStock - Inventory Management");
        primaryStage.show();
        
        loadProducts();
        checkLowStock();
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #2c3e50;");
        
        Label titleLabel = new Label("SmartStock");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search products...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, old, newVal) -> searchProducts(newVal));
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> loadProducts());
        
        topBar.getChildren().addAll(titleLabel, spacer, searchField, refreshBtn);
        return topBar;
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #34495e;");
        
        Button productsBtn = createSidebarButton("Products");
        Button categoriesBtn = createSidebarButton("Categories");
        Button suppliersBtn = createSidebarButton("Suppliers");
        Button transactionsBtn = createSidebarButton("Transactions");
        Button lowStockBtn = createSidebarButton("Low Stock");
        
        productsBtn.setOnAction(e -> showProductView());
        categoriesBtn.setOnAction(e -> showCategoryView());
        suppliersBtn.setOnAction(e -> showSupplierView());
        transactionsBtn.setOnAction(e -> showTransactionView());
        lowStockBtn.setOnAction(e -> showLowStockView());
        
        sidebar.getChildren().addAll(productsBtn, categoriesBtn, suppliersBtn, transactionsBtn, lowStockBtn);
        return sidebar;
    }
    
    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(180);
        btn.setPrefHeight(40);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; " +
                    "-fx-font-size: 14px; -fx-alignment: center-left; -fx-padding: 10;");
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-background-color: #2c3e50;"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle() + "-fx-background-color: transparent;"));
        return btn;
    }
    
    private VBox createProductView() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Products");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        
        Button addBtn = new Button("Add Product");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        addBtn.setOnAction(e -> showAddProductDialog());
        
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        editBtn.setOnAction(e -> editSelectedProduct());
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteSelectedProduct());
        
        Button stockBtn = new Button("Stock In/Out");
        stockBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        stockBtn.setOnAction(e -> showStockTransactionDialog());
        
        toolbar.getChildren().addAll(addBtn, editBtn, deleteBtn, stockBtn);
        
        productTable = new TableView<>();
        productTable.setItems(productList);
        
        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("productId"));
        idCol.setPrefWidth(50);
        
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Product, String> skuCol = new TableColumn<>("SKU");
        skuCol.setCellValueFactory(new PropertyValueFactory<>("sku"));
        skuCol.setPrefWidth(120);
        
        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        categoryCol.setPrefWidth(120);
        
        TableColumn<Product, String> supplierCol = new TableColumn<>("Supplier");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        supplierCol.setPrefWidth(150);
        
        TableColumn<Product, Number> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        priceCol.setPrefWidth(100);
        
        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("currentStock"));
        stockCol.setPrefWidth(80);
        stockCol.setCellFactory(col -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    Product product = getTableView().getItems().get(getIndex());
                    if (product.isLowStock()) {
                        setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        TableColumn<Product, Integer> minStockCol = new TableColumn<>("Min Stock");
        minStockCol.setCellValueFactory(new PropertyValueFactory<>("minimumStock"));
        minStockCol.setPrefWidth(80);
        
        productTable.getColumns().addAll(idCol, nameCol, skuCol, categoryCol, supplierCol, priceCol, stockCol, minStockCol);
        
        view.getChildren().addAll(titleLabel, toolbar, productTable);
        VBox.setVgrow(productTable, Priority.ALWAYS);
        
        return view;
    }
    
    private void loadProducts() {
        try {
            List<Product> products = productDAO.findAll();
            productList.clear();
            productList.addAll(products);
        } catch (SQLException e) {
            AlertUtil.showError("Database Error", "Failed to load products: " + e.getMessage());
        }
    }
    
    private void searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            loadProducts();
            return;
        }
        
        try {
            List<Product> products = productDAO.searchProducts(keyword);
            productList.clear();
            productList.addAll(products);
        } catch (SQLException e) {
            AlertUtil.showError("Database Error", "Failed to search products: " + e.getMessage());
        }
    }
    
    private void checkLowStock() {
        try {
            List<Product> lowStockProducts = productDAO.findLowStock();
            if (!lowStockProducts.isEmpty()) {
                Notifications.create()
                    .title("Low Stock Alert")
                    .text(lowStockProducts.size() + " product(s) are low on stock!")
                    .showWarning();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void showAddProductDialog() {
        ProductFormDialog dialog = new ProductFormDialog(primaryStage, null, categoryDAO, supplierDAO);
        dialog.showAndWait().ifPresent(product -> {
            try {
                productDAO.save(product);
                loadProducts();
                AlertUtil.showInfo("Success", "Product added successfully!");
            } catch (SQLException e) {
                AlertUtil.showError("Database Error", "Failed to add product: " + e.getMessage());
            }
        });
    }
    
    private void editSelectedProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a product to edit.");
            return;
        }
        
        ProductFormDialog dialog = new ProductFormDialog(primaryStage, selected, categoryDAO, supplierDAO);
        dialog.showAndWait().ifPresent(product -> {
            try {
                productDAO.update(product);
                loadProducts();
                AlertUtil.showInfo("Success", "Product updated successfully!");
            } catch (SQLException e) {
                AlertUtil.showError("Database Error", "Failed to update product: " + e.getMessage());
            }
        });
    }
    
    private void deleteSelectedProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a product to delete.");
            return;
        }
        
        if (AlertUtil.showConfirmation("Confirm Delete", 
            "Are you sure you want to delete " + selected.getName() + "?")) {
            try {
                productDAO.delete(selected.getProductId());
                loadProducts();
                AlertUtil.showInfo("Success", "Product deleted successfully!");
            } catch (SQLException e) {
                AlertUtil.showError("Database Error", "Failed to delete product: " + e.getMessage());
            }
        }
    }
    
    private void showStockTransactionDialog() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a product for stock transaction.");
            return;
        }
        
        StockTransactionDialog dialog = new StockTransactionDialog(primaryStage, selected);
        dialog.showAndWait().ifPresent(transaction -> {
            try {
                transactionDAO.save(transaction);
                loadProducts();
                AlertUtil.showInfo("Success", "Stock transaction completed!");
            } catch (SQLException e) {
                AlertUtil.showError("Database Error", "Failed to process transaction: " + e.getMessage());
            }
        });
    }
    
    private void showProductView() {
        mainLayout.setCenter(createProductView());
        loadProducts();
    }
    
    private void showCategoryView() {
        CategoryController controller = new CategoryController(mainLayout, categoryDAO);
        controller.show();
    }
    
    private void showSupplierView() {
        SupplierController controller = new SupplierController(mainLayout, supplierDAO);
        controller.show();
    }
    
    private void showTransactionView() {
        TransactionController controller = new TransactionController(mainLayout, transactionDAO);
        controller.show();
    }
    
    private void showLowStockView() {
        LowStockController controller = new LowStockController(mainLayout, productDAO);
        controller.show();
    }
}