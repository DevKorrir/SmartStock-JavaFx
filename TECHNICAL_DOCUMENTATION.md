# SmartStock Technical Documentation

## Object-Oriented Programming II Course Project

**Student Project Documentation**

This document provides a comprehensive technical explanation of the SmartStock Inventory Management System, demonstrating the application of Object-Oriented Programming principles, design patterns, and Java enterprise development concepts.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Architecture and Design Patterns](#2-architecture-and-design-patterns)
3. [Java Module System](#3-java-module-system)
4. [The Launcher Class](#4-the-launcher-class)
5. [Model Layer](#5-model-layer)
6. [Data Access Object Layer](#6-data-access-object-layer)
7. [Database Connection](#7-database-connection)
8. [Controller Layer](#8-controller-layer)
9. [Utility Classes](#9-utility-classes)
10. [OOP Principles Applied](#10-oop-principles-applied)
11. [Conclusion](#11-conclusion)

---

## 1. Project Overview

SmartStock is a desktop inventory management application built using JavaFX. The application allows users to manage products, categories, suppliers, and track stock transactions. It demonstrates real-world application development using Java's modern features including the module system, generics, lambda expressions, and design patterns.

### Technologies Used

| Technology | Purpose |
|------------|---------|
| Java 21 | Core programming language |
| JavaFX 21 | GUI framework |
| MariaDB | Relational database |
| JDBC | Database connectivity |
| Gradle | Build automation |
| Java Module System (JPMS) | Modular architecture |

---

## 2. Architecture and Design Patterns

### 2.1 Model-View-Controller (MVC) Pattern

The application follows the MVC architectural pattern, which separates the application into three interconnected components:

```
┌─────────────────────────────────────────────────────────────────┐
│                         SmartStock Architecture                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ┌──────────────┐    ┌──────────────┐    ┌──────────────┐     │
│   │    MODEL     │    │     VIEW     │    │  CONTROLLER  │     │
│   │              │    │              │    │              │     │
│   │  Product     │◄───│   JavaFX     │◄───│   Main       │     │
│   │  Category    │    │   FXML/UI    │    │   Controller │     │
│   │  Supplier    │    │   Components │    │              │     │
│   │  Transaction │    │              │    │              │     │
│   └──────┬───────┘    └──────────────┘    └──────┬───────┘     │
│          │                                        │              │
│          │         ┌────────────────┐            │              │
│          └────────►│      DAO       │◄───────────┘              │
│                    │   (Data Access │                           │
│                    │    Objects)    │                           │
│                    └───────┬────────┘                           │
│                            │                                     │
│                    ┌───────▼────────┐                           │
│                    │    Database    │                           │
│                    │   Connection   │                           │
│                    │   (Singleton)  │                           │
│                    └───────┬────────┘                           │
│                            │                                     │
│                    ┌───────▼────────┐                           │
│                    │    MariaDB     │                           │
│                    └────────────────┘                           │
└─────────────────────────────────────────────────────────────────┘
```

**Benefits of MVC:**
- **Separation of Concerns**: Each component has a specific responsibility
- **Maintainability**: Changes in one layer do not affect others
- **Testability**: Components can be tested independently
- **Reusability**: Models and DAOs can be reused across different views

### 2.2 Singleton Pattern

The Singleton pattern ensures that a class has only one instance throughout the application's lifecycle. This pattern is implemented in the `DatabaseConnection` class.

**Why Singleton for Database Connection?**
- Prevents multiple database connections being opened unnecessarily
- Conserves system resources
- Provides a global access point to the database
- Ensures thread-safe connection management

### 2.3 Data Access Object (DAO) Pattern

The DAO pattern abstracts and encapsulates all database operations. It provides a clean separation between business logic and data persistence.

**Benefits:**
- Database operations are centralized
- Easy to switch databases without changing business logic
- Promotes code reusability
- Simplifies unit testing

---

## 3. Java Module System

### 3.1 What is module-info.java?

The `module-info.java` file was introduced in Java 9 as part of the Java Platform Module System (JPMS). It defines a module, which is a named, self-describing collection of code and data.

### 3.2 Our module-info.java Explained

```java
module com.example.smartstock {
    // Required dependencies - modules this application needs
    requires javafx.controls;      // JavaFX UI controls (Button, TextField, etc.)
    requires javafx.fxml;          // FXML loader for declarative UI
    requires javafx.graphics;      // Core JavaFX graphics (Application class)
    requires java.sql;             // JDBC API for database operations
    requires org.mariadb.jdbc;     // MariaDB driver
    requires org.controlsfx.controls;  // Extended UI controls library

    // Opens packages for reflection access (needed by JavaFX)
    opens com.example.smartstock to javafx.fxml;
    opens com.example.smartstock.controllers to javafx.fxml;
    opens com.example.smartstock.models to javafx.base;

    // Exports - packages accessible to other modules
    exports com.example.smartstock;
    exports com.example.smartstock.controllers;
    exports com.example.smartstock.dao;
    exports com.example.smartstock.models;
    exports com.example.smartstock.util;
    exports com.example.smartstock.db;
}
```

### 3.3 Key Concepts

| Keyword | Purpose |
|---------|---------|
| `module` | Declares the module name |
| `requires` | Specifies dependencies on other modules |
| `opens` | Allows reflection access to a package (required for JavaFX property binding) |
| `exports` | Makes a package accessible to other modules |

### 3.4 Why Use Modules?

1. **Strong Encapsulation**: Only explicitly exported packages are accessible
2. **Reliable Configuration**: Missing dependencies are detected at compile time
3. **Improved Security**: Internal implementation details are hidden
4. **Better Performance**: JVM can optimize based on module boundaries
5. **Reduced Memory Footprint**: Only required modules are loaded

---

## 4. The Launcher Class

### 4.1 Why Do We Need a Launcher?

```java
package com.example.smartstock;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
```

### 4.2 The Problem It Solves

When using the Java Module System with JavaFX, there is a restriction: the main class that extends `javafx.application.Application` cannot be directly launched if it is in a named module. This is due to how the JavaFX runtime initializes the application.

### 4.3 How It Works

```
┌────────────────────────────────────────────────────────────────┐
│                    Application Startup Flow                     │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│   JVM starts                                                   │
│       │                                                         │
│       ▼                                                         │
│   ┌──────────────┐                                             │
│   │   Launcher   │  ◄── Entry point (main method)              │
│   │    .main()   │                                             │
│   └──────┬───────┘                                             │
│          │                                                      │
│          │ Application.launch(MainApp.class, args)             │
│          ▼                                                      │
│   ┌──────────────┐                                             │
│   │   JavaFX     │  ◄── JavaFX toolkit initializes             │
│   │   Runtime    │                                             │
│   └──────┬───────┘                                             │
│          │                                                      │
│          │ Creates instance of MainApp                         │
│          ▼                                                      │
│   ┌──────────────┐                                             │
│   │   MainApp    │  ◄── Application class                      │
│   │   .start()   │                                             │
│   └──────┬───────┘                                             │
│          │                                                      │
│          │ Initializes UI                                       │
│          ▼                                                      │
│   ┌──────────────┐                                             │
│   │  Application │  ◄── User can now interact                  │
│   │   Running    │                                             │
│   └──────────────┘                                             │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
```

### 4.4 Key Points

- The `Launcher` class does NOT extend `Application`
- It delegates to `Application.launch()` with the actual Application class
- This workaround is necessary for modular JavaFX applications
- Without it, you would get: `Error: JavaFX runtime components are missing`

---

## 5. Model Layer

### 5.1 What are Models?

Models are Plain Old Java Objects (POJOs) that represent the data entities in our application. They encapsulate data and provide getter/setter methods for accessing and modifying that data.

### 5.2 Product Model Example

```java
package com.example.smartstock.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    // Private fields - Encapsulation
    private Integer productId;
    private String name;
    private String description;
    private String sku;
    private Integer categoryId;
    private Integer supplierId;
    private BigDecimal unitPrice;
    private Integer currentStock;
    private Integer minimumStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Display fields (not stored in database)
    private String categoryName;
    private String supplierName;
    
    // Default constructor
    public Product() {
        this.unitPrice = BigDecimal.ZERO;
        this.currentStock = 0;
        this.minimumStock = 0;
    }
    
    // Getters and Setters
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    // ... additional getters/setters ...
    
    // Business logic method
    public boolean isLowStock() {
        return currentStock <= minimumStock;
    }
}
```

### 5.3 OOP Principles in Models

| Principle | Implementation |
|-----------|----------------|
| **Encapsulation** | All fields are `private`, accessed only through getters/setters |
| **Data Hiding** | Internal representation is hidden from external classes |
| **Validation** | Setters can include validation logic |
| **Business Logic** | Methods like `isLowStock()` encapsulate domain rules |

### 5.4 Nested Enum Example

```java
public class StockTransaction {
    private TransactionType transactionType;
    
    // Nested enum - groups related constants
    public enum TransactionType {
        IN, OUT
    }
}
```

**Benefits of Enums:**
- Type-safe constants
- Prevents invalid values
- Self-documenting code
- Can include methods and fields

---

## 6. Data Access Object Layer

### 6.1 BaseDAO - Abstract Class

The `BaseDAO` is an abstract class that provides common functionality for all DAO classes.

```java
package com.example.smartstock.dao;

import com.example.smartstock.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDAO {
    
    // Protected method - accessible by subclasses only
    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
    
    // Utility method for resource cleanup
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
```

### 6.2 OOP Concepts Demonstrated

| Concept | Implementation |
|---------|----------------|
| **Abstract Class** | Cannot be instantiated, provides base functionality |
| **Protected Access** | Methods visible to subclasses and same package |
| **Varargs** | `AutoCloseable... resources` accepts variable number of arguments |
| **Polymorphism** | `AutoCloseable` works with Connection, Statement, ResultSet |

### 6.3 ProductDAO - Concrete Implementation

```java
public class ProductDAO extends BaseDAO {
    
    public List<Product> findAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name, s.name as supplier_name " +
                    "FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                    "ORDER BY p.name";
        
        // Try-with-resources - automatic resource management
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }
    
    public Optional<Product> findById(Integer id) throws SQLException {
        // ... uses Optional for null safety
        return Optional.empty();
    }
    
    public Product save(Product product) throws SQLException {
        String sql = "INSERT INTO products (...) VALUES (?, ?, ?, ...)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, 
                 Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, product.getName());
            // ... set other parameters
            stmt.executeUpdate();
            
            // Retrieve auto-generated ID
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    product.setProductId(keys.getInt(1));
                }
            }
        }
        return product;
    }
    
    // Private helper method - encapsulates mapping logic
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setName(rs.getString("name"));
        // ... map other fields
        return product;
    }
}
```

### 6.4 Key Java Features Used

| Feature | Example | Purpose |
|---------|---------|---------|
| **Generics** | `List<Product>` | Type-safe collections |
| **Optional** | `Optional<Product>` | Null-safe return values |
| **Try-with-resources** | `try (Connection conn = ...)` | Automatic resource cleanup |
| **Prepared Statements** | `conn.prepareStatement(sql)` | SQL injection prevention |
| **Inheritance** | `extends BaseDAO` | Code reuse |

---

## 7. Database Connection

### 7.1 Singleton Pattern Implementation

```java
package com.example.smartstock.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    // Static instance - only one will exist
    private static DatabaseConnection instance;
    private final Properties properties;
    
    // Private constructor - prevents external instantiation
    private DatabaseConnection() {
        properties = new Properties();
        loadProperties();
    }
    
    // Thread-safe lazy initialization with double-checked locking
    public static DatabaseConnection getInstance() {
        if (instance == null) {                    // First check (no locking)
            synchronized (DatabaseConnection.class) {  // Acquire lock
                if (instance == null) {            // Second check (with lock)
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find db.properties");
            }
            properties.load(input);
            // Load JDBC driver class
            Class.forName(properties.getProperty("db.driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            properties.getProperty("db.url"),
            properties.getProperty("db.username"),
            properties.getProperty("db.password")
        );
    }
    
    public void testConnection() throws SQLException {
        try (Connection conn = getConnection()) {
            System.out.println("Database connection successful!");
        }
    }
}
```

### 7.2 Double-Checked Locking Explained

```
┌────────────────────────────────────────────────────────────────┐
│              Double-Checked Locking Pattern                     │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│   Thread A                          Thread B                   │
│      │                                  │                       │
│      ▼                                  ▼                       │
│   instance == null?                instance == null?           │
│   (YES - first check)              (YES - first check)         │
│      │                                  │                       │
│      ▼                                  │                       │
│   synchronized(lock)                    │ (waiting)             │
│      │                                  │                       │
│      ▼                                  │                       │
│   instance == null?                     │                       │
│   (YES - second check)                  │                       │
│      │                                  │                       │
│      ▼                                  │                       │
│   CREATE instance                       │                       │
│      │                                  │                       │
│      ▼                                  │                       │
│   release lock                          │                       │
│                                         ▼                       │
│                               synchronized(lock)               │
│                                         │                       │
│                                         ▼                       │
│                               instance == null?                │
│                               (NO - already created)           │
│                                         │                       │
│                                         ▼                       │
│                               return existing instance         │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
```

### 7.3 Why This Pattern?

- **Thread Safety**: Multiple threads cannot create multiple instances
- **Performance**: Lock acquired only when instance is null (first time)
- **Lazy Initialization**: Instance created only when first needed

---

## 8. Controller Layer

### 8.1 MainController

The `MainController` is the primary controller that manages the main application window and coordinates between different views.

```java
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
        scene.getStylesheets().add(
            getClass().getResource("/styles/main.css").toExternalForm()
        );
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("SmartStock - Inventory Management");
        primaryStage.show();
        
        loadProducts();
        checkLowStock();
    }
}
```

### 8.2 Key Controller Responsibilities

| Responsibility | Method Example |
|----------------|----------------|
| UI Creation | `createTopBar()`, `createSidebar()` |
| Event Handling | `deleteBtn.setOnAction(e -> deleteSelectedProduct())` |
| Data Loading | `loadProducts()` |
| View Navigation | `showCategoryView()`, `showSupplierView()` |
| User Feedback | `AlertUtil.showInfo()`, notifications |

### 8.3 Lambda Expressions for Event Handling

```java
// Traditional anonymous inner class
button.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent e) {
        loadProducts();
    }
});

// Lambda expression (concise, modern)
button.setOnAction(e -> loadProducts());

// Lambda with multiple statements
searchField.textProperty().addListener((observable, oldValue, newValue) -> {
    searchProducts(newValue);
});
```

### 8.4 JavaFX Property Binding

```java
// ObservableList automatically updates the TableView
private ObservableList<Product> productList = FXCollections.observableArrayList();

// When products are added/removed, the UI updates automatically
productList.clear();
productList.addAll(products);
```

---

## 9. Utility Classes

### 9.1 AlertUtil - Static Utility Class

```java
package com.example.smartstock.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class AlertUtil {
    
    public static void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
```

### 9.2 Design Decisions

| Decision | Reason |
|----------|--------|
| **Static Methods** | No instance needed, utility functions |
| **Method Overloading** | Different alert types with same signature pattern |
| **Boolean Return** | `showConfirmation()` returns user's decision |
| **Optional Handling** | Safe handling of potentially empty result |

---

## 10. OOP Principles Applied

### 10.1 The Four Pillars of OOP

#### Encapsulation

```java
public class Product {
    // Private fields - data is hidden
    private Integer currentStock;
    private Integer minimumStock;
    
    // Public methods to access/modify data
    public Integer getCurrentStock() { 
        return currentStock; 
    }
    
    public void setCurrentStock(Integer currentStock) { 
        // Can add validation here
        if (currentStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        this.currentStock = currentStock; 
    }
}
```

#### Inheritance

```java
// Base class
public abstract class BaseDAO {
    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
}

// Derived classes inherit common functionality
public class ProductDAO extends BaseDAO {
    // Inherits getConnection() method
    public List<Product> findAll() throws SQLException {
        try (Connection conn = getConnection()) {  // Using inherited method
            // ...
        }
    }
}

public class CategoryDAO extends BaseDAO {
    // Also inherits getConnection()
}
```

#### Polymorphism

```java
// Interface polymorphism with AutoCloseable
protected void closeResources(AutoCloseable... resources) {
    for (AutoCloseable resource : resources) {
        // Works with Connection, Statement, ResultSet
        // All implement AutoCloseable
        resource.close();
    }
}

// Method overriding in TableCell
typeCol.setCellFactory(col -> new TableCell<>() {
    @Override
    protected void updateItem(TransactionType item, boolean empty) {
        super.updateItem(item, empty);  // Call parent method
        // Custom implementation
    }
});
```

#### Abstraction

```java
// Abstract class hides complexity of database access
public abstract class BaseDAO {
    // Subclasses don't need to know HOW connection is obtained
    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
}

// DAO abstracts SQL operations from controllers
public class ProductDAO {
    public List<Product> findAll() throws SQLException {
        // Controller doesn't need to know SQL
    }
}
```

### 10.2 SOLID Principles

| Principle | Application in Project |
|-----------|------------------------|
| **S**ingle Responsibility | Each class has one purpose (DAO for data, Controller for UI) |
| **O**pen/Closed | New DAOs can be added without modifying BaseDAO |
| **L**iskov Substitution | ProductDAO, CategoryDAO can replace BaseDAO reference |
| **I**nterface Segregation | Small, focused interfaces (AutoCloseable) |
| **D**ependency Inversion | Controllers depend on DAO abstractions |

---

## 11. Conclusion

### 11.1 Summary of Concepts Applied

This project demonstrates proficiency in:

1. **Object-Oriented Design**
   - Encapsulation, Inheritance, Polymorphism, Abstraction
   - SOLID principles
   - Design patterns (Singleton, DAO, MVC)

2. **Java Language Features**
   - Module system (JPMS)
   - Generics and Collections
   - Lambda expressions
   - Try-with-resources
   - Optional for null safety
   - Enums for type safety

3. **JavaFX UI Development**
   - Scene graph architecture
   - Event handling
   - Property binding
   - CSS styling

4. **Database Programming**
   - JDBC for database connectivity
   - PreparedStatement for security
   - Transaction handling
   - Connection pooling concepts

5. **Software Engineering**
   - Separation of concerns
   - Code reusability
   - Error handling
   - Configuration management

### 11.2 Skills Demonstrated

| Category | Skills |
|----------|--------|
| **Programming** | Java 21, JavaFX, SQL |
| **Architecture** | MVC, layered architecture |
| **Patterns** | Singleton, DAO, Factory |
| **Database** | MariaDB, JDBC, SQL queries |
| **Tools** | Gradle, Git, IntelliJ IDEA |

---

## Appendix: Class Diagram

```
┌───────────────────────────────────────────────────────────────────────┐
│                        SmartStock Class Diagram                        │
├───────────────────────────────────────────────────────────────────────┤
│                                                                        │
│  ┌─────────────────┐                      ┌─────────────────┐         │
│  │   <<abstract>>  │                      │    Launcher     │         │
│  │     BaseDAO     │                      ├─────────────────┤         │
│  ├─────────────────┤                      │ +main(args)     │         │
│  │ #getConnection()│                      └────────┬────────┘         │
│  │ #closeResources()                               │                   │
│  └────────┬────────┘                               ▼                   │
│           │                               ┌─────────────────┐         │
│     ┌─────┴─────┬─────────┬─────────┐    │     MainApp     │         │
│     │           │         │         │    │  <<Application>>│         │
│     ▼           ▼         ▼         ▼    ├─────────────────┤         │
│ ┌────────┐ ┌────────┐ ┌────────┐ ┌────┐ │ +start(stage)   │         │
│ │Product │ │Category│ │Supplier│ │Txn │ └────────┬────────┘         │
│ │  DAO   │ │  DAO   │ │  DAO   │ │DAO │          │                   │
│ └────────┘ └────────┘ └────────┘ └────┘          ▼                   │
│     │           │         │         │    ┌─────────────────┐         │
│     └───────────┴────┬────┴─────────┘    │ MainController  │         │
│                      │                    ├─────────────────┤         │
│                      ▼                    │ -productDAO     │         │
│              ┌──────────────┐            │ -categoryDAO    │         │
│              │  <<Singleton>>│            │ -supplierDAO    │         │
│              │ DatabaseConn. │◄───────────│ -transactionDAO │         │
│              ├──────────────┤            │ +initialize()   │         │
│              │ -instance    │            └─────────────────┘         │
│              │ +getInstance()│                                        │
│              │ +getConnection()                                       │
│              └──────────────┘                                        │
│                                                                        │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐      │
│  │  Product   │  │  Category  │  │  Supplier  │  │Transaction │      │
│  │  <<Model>> │  │  <<Model>> │  │  <<Model>> │  │  <<Model>> │      │
│  ├────────────┤  ├────────────┤  ├────────────┤  ├────────────┤      │
│  │ -productId │  │ -categoryId│  │ -supplierId│  │ -txnId     │      │
│  │ -name      │  │ -name      │  │ -name      │  │ -productId │      │
│  │ -sku       │  │ -description│ │ -email     │  │ -type      │      │
│  │ -price     │  └────────────┘  │ -phone     │  │ -quantity  │      │
│  │ -stock     │                  └────────────┘  └────────────┘      │
│  └────────────┘                                                       │
│                                                                        │
└───────────────────────────────────────────────────────────────────────┘
```

---

*Document prepared for OOP II course assessment.*
