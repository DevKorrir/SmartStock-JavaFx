package com.example.smartstock.dao;

import com.example.smartstock.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO extends BaseDAO {
    
    public List<Product> findAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name, s.name as supplier_name " +
                    "FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                    "ORDER BY p.name";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }
    
    public List<Product> findLowStock() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM low_stock_products";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setSku(rs.getString("sku"));
                product.setCategoryName(rs.getString("category"));
                product.setSupplierName(rs.getString("supplier"));
                product.setCurrentStock(rs.getInt("current_stock"));
                product.setMinimumStock(rs.getInt("minimum_stock"));
                product.setUnitPrice(rs.getBigDecimal("unit_price"));
                products.add(product);
            }
        }
        return products;
    }
    
    public Optional<Product> findById(Integer id) throws SQLException {
        String sql = "SELECT p.*, c.name as category_name, s.name as supplier_name " +
                    "FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                    "WHERE p.product_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    public Product save(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, description, sku, category_id, supplier_id, " +
                    "unit_price, current_stock, minimum_stock) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setString(3, product.getSku());
            stmt.setObject(4, product.getCategoryId());
            stmt.setObject(5, product.getSupplierId());
            stmt.setBigDecimal(6, product.getUnitPrice());
            stmt.setInt(7, product.getCurrentStock());
            stmt.setInt(8, product.getMinimumStock());
            
            stmt.executeUpdate();
            
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    product.setProductId(keys.getInt(1));
                }
            }
        }
        return product;
    }
    
    public void update(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, description = ?, sku = ?, category_id = ?, " +
                    "supplier_id = ?, unit_price = ?, current_stock = ?, minimum_stock = ? " +
                    "WHERE product_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setString(3, product.getSku());
            stmt.setObject(4, product.getCategoryId());
            stmt.setObject(5, product.getSupplierId());
            stmt.setBigDecimal(6, product.getUnitPrice());
            stmt.setInt(7, product.getCurrentStock());
            stmt.setInt(8, product.getMinimumStock());
            stmt.setInt(9, product.getProductId());
            
            stmt.executeUpdate();
        }
    }
    
    public void updateStock(Integer productId, Integer newStock) throws SQLException {
        String sql = "UPDATE products SET current_stock = ? WHERE product_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newStock);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }
    
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public List<Product> searchProducts(String keyword) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name, s.name as supplier_name " +
                    "FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                    "WHERE p.name LIKE ? OR p.sku LIKE ? OR p.description LIKE ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }
        return products;
    }
    
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setSku(rs.getString("sku"));
        product.setCategoryId(rs.getObject("category_id", Integer.class));
        product.setSupplierId(rs.getObject("supplier_id", Integer.class));
        product.setUnitPrice(rs.getBigDecimal("unit_price"));
        product.setCurrentStock(rs.getInt("current_stock"));
        product.setMinimumStock(rs.getInt("minimum_stock"));
        product.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        product.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        product.setCategoryName(rs.getString("category_name"));
        product.setSupplierName(rs.getString("supplier_name"));
        return product;
    }
}