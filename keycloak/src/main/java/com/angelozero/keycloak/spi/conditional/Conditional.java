package com.angelozero.keycloak.spi.conditional;

import com.angelozero.keycloak.spi.auth.Authentication;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Conditional implements ConditionalAuthenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Conditional.class);

    @Override
    public boolean matchCondition(AuthenticationFlowContext context) {
        LOGGER.info("\n");
        try {
            var authenticationConfigEnableProp = context.getAuthenticationSession().getAuthNote(Authentication.CONFIG_ENABLE);
            var conditionalConfigEnableProp = context.getAuthenticatorConfig().getConfig().get(ConditionalFactory.ACCESS_CONFIG_VALUE);

            var authenticationConfigEnableValue = Boolean.parseBoolean(authenticationConfigEnableProp);
            var conditionalConfigEnableValue = Boolean.parseBoolean(conditionalConfigEnableProp);

            LOGGER.info("[Conditional] - Authentication config value ----> {}", authenticationConfigEnableValue);
            LOGGER.info("[Conditional] - Conditional config value -------> {}", conditionalConfigEnableProp);
            LOGGER.info("[Conditional] - Should access the sub flow ? ---> {}", authenticationConfigEnableValue && conditionalConfigEnableValue ? "Yes" : "No");

            return authenticationConfigEnableValue && conditionalConfigEnableValue;

        } catch (Exception ex) {
            LOGGER.error("[Conditional] - Something went wrong while validating the Conditional SPI - fail: {}", ex.getMessage());
            return false;
        }
    }

    @Override
    public void action(AuthenticationFlowContext authenticationFlowContext) {

    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    @Override
    public void close() {

    }
}
