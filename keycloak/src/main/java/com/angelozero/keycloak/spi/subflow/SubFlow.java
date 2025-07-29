package com.angelozero.keycloak.spi.subflow;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubFlow implements Authenticator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubFlow.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LOGGER.info("[SubFlow] - SPI");

        var subFlowConfigValue = context.getAuthenticatorConfig().getConfig().get(SubFlowFactory.CONFIG_VALUE);

        if (!subFlowConfigValue.isBlank()) {
            LOGGER.info("[SubFlow] - The value typed was ---> {}", subFlowConfigValue);

        } else {
            LOGGER.info("[SubFlow] - No value was typed");
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
