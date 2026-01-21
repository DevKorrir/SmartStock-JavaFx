# SmartStock

A desktop inventory management system built with JavaFX for small to medium-sized businesses. SmartStock provides comprehensive stock tracking, supplier management, and low-stock alerts to streamline your inventory operations.

---

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [License](#license)

---

## Features

### Product Management
- Add, edit, and delete products with detailed information
- Track SKU, pricing, and stock levels for each product
- Assign products to categories and suppliers
- Real-time product search functionality

### Inventory Tracking
- Record stock-in and stock-out transactions
- Maintain complete transaction history with references and notes
- Visual indicators for stock levels in product tables

### Low Stock Alerts
- Automatic detection of products below minimum stock threshold
- Desktop notifications when low stock is detected
- Dedicated low stock view for quick action

### Category Management
- Organize products into customizable categories
- Add descriptions to categories for better organization

### Supplier Management
- Maintain supplier contact information
- Track supplier email, phone, and address details
- Link products to their respective suppliers

---

## Technology Stack

| Component | Technology |
|-----------|------------|
| Language | Java 21 |
| UI Framework | JavaFX 21 |
| Build Tool | Gradle (Kotlin DSL) |
| Database | MariaDB |
| UI Controls | ControlsFX |

---

## Prerequisites

Before running SmartStock, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
- **MariaDB Server** (version 10.5 or higher recommended)
- **Gradle** (or use the included wrapper)

---

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/DevKorrir/SmartStock-JavaFx.git
   cd SmartStock-JavaFx
   ```

2. **Create the database**
   ```sql
   CREATE DATABASE smartstock_db;
   ```

3. **Configure database connection** (see [Configuration](#configuration))

4. **Build the project**
   ```bash
   ./gradlew build
   ```

---

## Configuration

1. Copy the example configuration file:
   ```bash
   cp src/main/resources/db.properties.example src/main/resources/db.properties
   ```

2. Edit `db.properties` with your database credentials:
   ```properties
   db.driver=org.mariadb.jdbc.Driver
   db.url=jdbc:mariadb://localhost:3306/smartstock_db
   db.username=your_username
   db.password=your_password
   ```

> **Note**: The `db.properties` file contains sensitive credentials and is excluded from version control via `.gitignore`.

---

## Running the Application

### Using Gradle

```bash
./gradlew run
```

### Building a Distribution

```bash
./gradlew jlink
```

This creates a distributable package in `build/distributions/`.

---

## Project Structure

```
smartstock/
├── src/main/java/com/example/smartstock/
│   ├── MainApp.java              # Application entry point
│   ├── Launcher.java             # Launcher for module compatibility
│   ├── controllers/              # UI controllers
│   │   ├── MainController.java
│   │   ├── CategoryController.java
│   │   ├── SupplierController.java
│   │   ├── TransactionController.java
│   │   ├── LowStockController.java
│   │   ├── ProductFormDialog.java
│   │   └── StockTransactionDialog.java
│   ├── dao/                      # Data Access Objects
│   │   ├── ProductDAO.java
│   │   ├── CategoryDAO.java
│   │   ├── SupplierDAO.java
│   │   └── StockTransactionDAO.java
│   ├── db/                       # Database connection
│   │   └── DatabaseConnection.java
│   ├── models/                   # Data models
│   │   ├── Product.java
│   │   ├── Category.java
│   │   ├── Supplier.java
│   │   └── StockTransaction.java
│   └── util/                     # Utility classes
│       └── AlertUtil.java
├── src/main/resources/
│   ├── styles/
│   │   └── main.css              # Application styling
│   └── db.properties.example     # Database config template
├── build.gradle.kts              # Build configuration
└── README.md
```

---

## Database Schema

The application uses the following core tables:

### Categories
| Column | Type | Description |
|--------|------|-------------|
| category_id | INT | Primary key |
| name | VARCHAR | Category name |
| description | TEXT | Category description |

### Suppliers
| Column | Type | Description |
|--------|------|-------------|
| supplier_id | INT | Primary key |
| name | VARCHAR | Supplier name |
| contact_person | VARCHAR | Contact person name |
| email | VARCHAR | Email address |
| phone | VARCHAR | Phone number |
| address | TEXT | Physical address |

### Products
| Column | Type | Description |
|--------|------|-------------|
| product_id | INT | Primary key |
| name | VARCHAR | Product name |
| sku | VARCHAR | Stock keeping unit |
| category_id | INT | Foreign key to categories |
| supplier_id | INT | Foreign key to suppliers |
| unit_price | DECIMAL | Price per unit |
| current_stock | INT | Current stock quantity |
| minimum_stock | INT | Low stock threshold |

### Stock Transactions
| Column | Type | Description |
|--------|------|-------------|
| transaction_id | INT | Primary key |
| product_id | INT | Foreign key to products |
| transaction_type | ENUM | IN or OUT |
| quantity | INT | Transaction quantity |
| transaction_date | DATETIME | Transaction timestamp |
| reference | VARCHAR | Reference number |
| notes | TEXT | Additional notes |

---


## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Open a Pull Request

---

## Support

For issues and feature requests, please open an issue on the GitHub repository.