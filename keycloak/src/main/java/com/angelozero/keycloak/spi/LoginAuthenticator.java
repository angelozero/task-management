package com.angelozero.keycloak.spi;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginAuthenticator implements Authenticator {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginAuthenticator.class);

    public static final String LOGIN_AUTHENTICATOR_ID = "login-authenticator-id";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LOGGER.info("[LoginAuthenticator] - Login Authenticator SPI");

        var userName = context.getHttpRequest().getDecodedFormParameters().getFirst("username");
        var password = context.getHttpRequest().getDecodedFormParameters().getFirst("password");

        LOGGER.info("[LoginAuthenticator] - User request data info:");
        LOGGER.info("[LoginAuthenticator] - USERNAME ---------------> {}", userName);
        LOGGER.info("[LoginAuthenticator] - PASSWORD ---------------> {}", password);

        if (userName.equals("admin") && password.equals("admin")) {
            LOGGER.info("[LoginAuthenticator] - User ADMIN authenticated with success");
            context.success();
            return;
        }

        LOGGER.info("[LoginAuthenticator] - User \"{}\" authenticated with success", context.getUser().getFirstName());
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
