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

    public static final String ID = "conditional-id";
    public static final Conditional SINGLETON = new Conditional();

    @Override
    public boolean matchCondition(AuthenticationFlowContext context) {
        LOGGER.info("[Conditional] - SPI");
        try {
            var authenticationConfigEnable = Boolean.parseBoolean(
                    context.getAuthenticatorConfig()
                            .getConfig()
                            .get(Authentication.CONFIG_ENABLE));

            var conditionalConfig = Boolean.parseBoolean(
                    context.getAuthenticatorConfig()
                            .getConfig().get(ConditionalFactory.ACCESS_CONFIG_VALUE));

            LOGGER.info("[Conditional] - Authentication config value ----> {}", authenticationConfigEnable);
            LOGGER.info("[Conditional] - Conditional config value -------> {}", conditionalConfig);
            LOGGER.info("[Conditional] - Should access the sub flow ? ---> {}", authenticationConfigEnable && conditionalConfig ? "Yes" : "No");

            return authenticationConfigEnable && conditionalConfig;

        } catch (Exception ex) {
            LOGGER.error("[Conditional] - Something went wrong while validating the conditional SPI - fail: {}", ex.getMessage());
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
