package com.example.smartstock.dao;

import com.example.smartstock.models.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupplierDAO extends BaseDAO {
    
    public List<Supplier> findAll() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY name";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        }
        return suppliers;
    }
    
    public Optional<Supplier> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSupplier(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    public Supplier save(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getPhone());
            stmt.setString(4, supplier.getEmail());
            stmt.setString(5, supplier.getAddress());
            
            stmt.executeUpdate();
            
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    supplier.setSupplierId(keys.getInt(1));
                }
            }
        }
        return supplier;
    }
    
    public void update(Supplier supplier) throws SQLException {
        String sql = "UPDATE suppliers SET name = ?, contact_person = ?, phone = ?, email = ?, address = ? WHERE supplier_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getPhone());
            stmt.setString(4, supplier.getEmail());
            stmt.setString(5, supplier.getAddress());
            stmt.setInt(6, supplier.getSupplierId());
            
            stmt.executeUpdate();
        }
    }
    
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(rs.getInt("supplier_id"));
        supplier.setName(rs.getString("name"));
        supplier.setContactPerson(rs.getString("contact_person"));
        supplier.setPhone(rs.getString("phone"));
        supplier.setEmail(rs.getString("email"));
        supplier.setAddress(rs.getString("address"));
        supplier.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        supplier.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return supplier;
    }
}