package com.angelozero.task.management.entity;

public record Token(String accessToken,
                    int expiresIn,
                    int refreshExpiresIn,
                    String refreshToken,
                    String tokenType,
                    String sessionState,
                    String scope) {
}
