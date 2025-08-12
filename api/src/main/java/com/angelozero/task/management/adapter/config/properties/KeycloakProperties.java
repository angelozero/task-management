package com.angelozero.task.management.adapter.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties
public class KeycloakProperties {

    @Value("${keycloak.auth.token.url}")
    private String url;

    @Value("${keycloak.auth.token.client-id}")
    private String clientId;

    @Value("${keycloak.auth.token.client-secret}")
    private String clientSecret;

    @Value("${keycloak.auth.token.grant.type}")
    private String grantType;
}
