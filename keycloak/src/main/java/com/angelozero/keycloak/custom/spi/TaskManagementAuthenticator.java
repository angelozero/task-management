package com.angelozero.keycloak.custom.spi;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskManagementAuthenticator implements Authenticator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskManagementAuthenticator.class);

    public static final String TASK_MANAGEMENT_AUTHENTICATOR_PROVIDER_ID = "task-management-authenticator-id";
    public static final String TASK_MANAGEMENT_CONFIG_ENABLE = "TASK_MANAGEMENT_CONFIG_ENABLE";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LOGGER.info("[TaskManagement] - Custom Authenticator SPI");

        var userName = context.getHttpRequest().getDecodedFormParameters().getFirst("username");
        var password = context.getHttpRequest().getDecodedFormParameters().getFirst("password");

        var clientMasterEnable = context.getAuthenticatorConfig() != null ?
                context.getAuthenticatorConfig().getConfig().get(TASK_MANAGEMENT_CONFIG_ENABLE) : true;

        LOGGER.info("[TaskManagement] - User request data info:");
        LOGGER.info("[TaskManagement] - USERNAME ---------------> {}", userName);
        LOGGER.info("[TaskManagement] - PASSWORD ---------------> {}", password);
        LOGGER.info("[TaskManagement] - IS ENABLE ? ------------> {}", clientMasterEnable);

        if (userName.equals("admin") && password.equals("admin")) {
            LOGGER.info("[TaskManagement] - User ADMIN authenticated with success");
            context.success();
            return;
        }

        LOGGER.info("[TaskManagement] - User \"{}\" authenticated with success", userName);
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
