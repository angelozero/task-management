package com.angelozero.keycloak.spi.conditional;

import org.keycloak.Config;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.Collections;
import java.util.List;

public class ConditionalFactory implements ConditionalAuthenticatorFactory {

    public static final String ACCESS_CONFIG_VALUE = "accessConfigValue";
    public static final String ID = "conditional-id";
    private static final Conditional CONDITIONAL_INSTANCE = new Conditional();

    @Override
    public ConditionalAuthenticator getSingleton() {
        return CONDITIONAL_INSTANCE;
    }

    @Override
    public String getDisplayType() {
        return "Conditional SPI";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public String getHelpText() {
        return "This is a conditional SPI";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return ID;
    }

    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = Collections.unmodifiableList(
            ProviderConfigurationBuilder.create()
                    .property()
                    .name(ACCESS_CONFIG_VALUE)
                    .label("Should access the sub flow ?")
                    .type(ProviderConfigProperty.BOOLEAN_TYPE)
                    .add()
                    .build()
    );

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.DISABLED
    };
}
