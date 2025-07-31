package com.angelozero.keycloak.spi.message;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Message implements Authenticator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Message.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LOGGER.info("\n");

        var subFlowConfigValue = context.getAuthenticatorConfig().getConfig().get(MessageFactory.CONFIG_VALUE);

        if (!subFlowConfigValue.isBlank()) {
            LOGGER.info("[Message] - The final message is ---> {}", subFlowConfigValue);

        } else {
            LOGGER.info("[Message] - There is no final message to display.");
        }

        context.success();
    }

    @Override
    public void action(AuthenticationFlowContext authenticationFlowContext) {

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
