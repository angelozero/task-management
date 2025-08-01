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


    public static final String CONFIG_ENABLE_CONDITIONAL_SPI = "CONFIG_ENABLE_CONDITIONAL_SPI";
    public static final String CONFIG_ENABLE_ACCESS_TOKEN_MAPPER = "CONFIG_ENABLE_ACCESS_TOKEN_MAPPER";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LOGGER.info("\n");

        var userName = context.getHttpRequest().getDecodedFormParameters().getFirst("username");
        var password = context.getHttpRequest().getDecodedFormParameters().getFirst("password");

        var configEnable = context.getAuthenticatorConfig() != null ?
                context.getAuthenticatorConfig().getConfig().get(CONFIG_ENABLE_CONDITIONAL_SPI) : false;

        context.getAuthenticationSession().setAuthNote(CONFIG_ENABLE_CONDITIONAL_SPI, String.valueOf(configEnable));

        LOGGER.info("[Authentication] - User request data info:");
        LOGGER.info("[Authentication] - Username ------------> {}", userName);
        LOGGER.info("[Authentication] - Password ------------> {}", password);
        LOGGER.info("[Authentication] - Spi configuration ---> {}", configEnable);

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
