package com.angelozero.task.management.adapter.dataprovider;

import com.angelozero.task.management.adapter.config.properties.KeycloakProperties;
import com.angelozero.task.management.adapter.dataprovider.mapper.KeycloakDataProviderMapper;
import com.angelozero.task.management.adapter.dataprovider.rest.response.keycloak.KeycloakTokenResponse;
import com.angelozero.task.management.entity.Token;
import com.angelozero.task.management.usecase.exception.RestDataProviderException;
import com.angelozero.task.management.usecase.gateway.TokenGateway;
import com.angelozero.task.management.usecase.util.GetRestTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@AllArgsConstructor
public class TokenByRestDataProvider implements TokenGateway {

    private final GetRestTemplate getRestTemplate;
    private final KeycloakProperties keycloakProperties;
    private final KeycloakDataProviderMapper keycloakDataProviderMapper;

    @Override
    public Token generateToken(String username, String password) {
        try {

            var restTemplate = getRestTemplate.execute();
            var url = keycloakProperties.getUrl();
            var clientId = keycloakProperties.getClientId();
            var clientSecret = keycloakProperties.getClientSecret();
            var grantType = keycloakProperties.getGrantType();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            String body = String.format(
                    "username=%s&password=%s&client_id=%s&client_secret=%s&grant_type=%s",
                    username,
                    password,
                    clientId,
                    clientSecret,
                    grantType);

            var response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), KeycloakTokenResponse.class);

            var token = keycloakDataProviderMapper.toToken(response.getBody());

            log.info("Token was generated with success - {}", token);
            return token;

        } catch (Exception ex) {
            log.error("Fail to generate access token in Keycloak {}", ex.getMessage());
            throw new RestDataProviderException("Fail to generate access token in Keycloak " + ex.getMessage());
        }
    }
}
