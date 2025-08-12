package com.angelozero.task.management.adapter.controller.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(@JsonProperty("access_token") String accessToken,
                            @JsonProperty("expires_in") int expiresIn,
                            @JsonProperty("refresh_expires_in") int refreshExpiresIn,
                            @JsonProperty("refresh_token") String refreshToken,
                            @JsonProperty("token_type") String tokenType,
                            @JsonProperty("session_state") String sessionState,
                            @JsonProperty("scope") String scope) {
}
