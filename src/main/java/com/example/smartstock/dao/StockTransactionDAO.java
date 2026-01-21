package com.example.smartstock.dao;

import com.example.smartstock.models.StockTransaction;
import com.example.smartstock.models.StockTransaction.TransactionType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockTransactionDAO extends BaseDAO {
    private final ProductDAO productDAO = new ProductDAO();
    
    public List<StockTransaction> findAll() throws SQLException {
        List<StockTransaction> transactions = new ArrayList<>();
        String sql = "SELECT st.*, p.name as product_name " +
                    "FROM stock_transactions st " +
                    "JOIN products p ON st.product_id = p.product_id " +
                    "ORDER BY st.transaction_date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        return transactions;
    }
    
    public StockTransaction save(StockTransaction transaction) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            
            // Insert transaction
            String sql = "INSERT INTO stock_transactions (product_id, transaction_type, quantity, reference, notes) " +
                        "VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, transaction.getProductId());
                stmt.setString(2, transaction.getTransactionType().name());
                stmt.setInt(3, transaction.getQuantity());
                stmt.setString(4, transaction.getReference());
                stmt.setString(5, transaction.getNotes());
                
                stmt.executeUpdate();
                
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        transaction.setTransactionId(keys.getInt(1));
                    }
                }
            }
            
            // Update product stock
            var product = productDAO.findById(transaction.getProductId())
                .orElseThrow(() -> new SQLException("Product not found"));
            
            int newStock = product.getCurrentStock();
            if (transaction.getTransactionType() == TransactionType.IN) {
                newStock += transaction.getQuantity();
            } else {
                newStock -= transaction.getQuantity();
                if (newStock < 0) {
                    throw new SQLException("Insufficient stock");
                }
            }
            
            productDAO.updateStock(transaction.getProductId(), newStock);
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return transaction;
    }
    
    public List<StockTransaction> findByProductId(Integer productId) throws SQLException {
        List<StockTransaction> transactions = new ArrayList<>();
        String sql = "SELECT st.*, p.name as product_name " +
                    "FROM stock_transactions st " +
                    "JOIN products p ON st.product_id = p.product_id " +
                    "WHERE st.product_id = ? " +
                    "ORDER BY st.transaction_date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        return transactions;
    }
    
    private StockTransaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        StockTransaction transaction = new StockTransaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setProductId(rs.getInt("product_id"));
        transaction.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
        transaction.setQuantity(rs.getInt("quantity"));
        transaction.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());
        transaction.setReference(rs.getString("reference"));
        transaction.setNotes(rs.getString("notes"));
        transaction.setProductName(rs.getString("product_name"));
        return transaction;
    }
}