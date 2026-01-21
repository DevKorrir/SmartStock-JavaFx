package com.example.smartstock.models;

import java.time.LocalDateTime;

public class StockTransaction {
    private Integer transactionId;
    private Integer productId;
    private TransactionType transactionType;
    private Integer quantity;
    private LocalDateTime transactionDate;
    private String reference;
    private String notes;
    
    // For display
    private String productName;
    
    public enum TransactionType {
        IN, OUT
    }
    
    public StockTransaction() {}
    
    // Getters and Setters
    public Integer getTransactionId() { return transactionId; }
    public void setTransactionId(Integer transactionId) { this.transactionId = transactionId; }
    
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    
    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
    
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}