package com.angelozero.keycloak.custom.spi.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordService {

    public static String generateHash(String password) {
        if (password != null && !password.isEmpty()) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(password.getBytes());

                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Error hashing password - Error: " + e.getMessage(), e);
            }
        }
        return password;
    }
}
