package com.example.smartstock.util;

import java.math.BigDecimal;

public class ValidationUtil {
    
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    public static boolean isValidPhone(String phone) {
        if (isNullOrEmpty(phone)) return false;
        return phone.matches("^\\+?[0-9]{10,15}$");
    }
    
    public static boolean isValidNumber(String str) {
        if (isNullOrEmpty(str)) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isValidDecimal(String str) {
        if (isNullOrEmpty(str)) return false;
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}