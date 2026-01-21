package com.example.smartstock.models;

import java.time.LocalDateTime;

public class Supplier {
    private Integer supplierId;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Supplier() {}
    
    public Supplier(Integer supplierId, String name, String contactPerson, 
                   String phone, String email, String address) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }
    
    // Getters and Setters
    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return name;
    }
}