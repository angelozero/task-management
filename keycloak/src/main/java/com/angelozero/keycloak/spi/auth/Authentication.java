package com.angelozero.keycloak.spi.auth;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Authentication implements Authenticator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Authentication.class);

    public static final String ID = "authentication-id";
    public static final String CONFIG_ENABLE = "AUTHENTICATION_CONFIG_ENABLE";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LOGGER.info("[Authentication] - SPI");

        var userName = context.getHttpRequest().getDecodedFormParameters().getFirst("username");
        var password = context.getHttpRequest().getDecodedFormParameters().getFirst("password");

        var taskManagementConfigEnable = context.getAuthenticatorConfig() != null ?
                context.getAuthenticatorConfig().getConfig().get(CONFIG_ENABLE) : false;

        LOGGER.info("[Authentication] - User request data info:");
        LOGGER.info("[Authentication] - USERNAME --------------------> {}", userName);
        LOGGER.info("[Authentication] - PASSWORD --------------------> {}", password);
        LOGGER.info("[Authentication] - ACCESS CONDITIONAL SPI ? ----> {}", taskManagementConfigEnable);

        if (userName.equals("admin") && password.equals("admin")) {
            LOGGER.info("[Authentication] - User ADMIN authenticated with success");
            context.success();
            return;
        }

        LOGGER.info("[Authentication] - User \"{}\" authenticated with success", userName);
        context.success();
    }

    @Override
    public void action(AuthenticationFlowContext context) {
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    @Override
    public void close() {

    }
}
