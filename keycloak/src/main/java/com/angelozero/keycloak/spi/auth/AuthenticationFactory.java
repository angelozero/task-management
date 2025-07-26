package com.angelozero.keycloak.spi.auth;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationFactory implements AuthenticatorFactory {

    private static final Authentication AUTHENTICATION_INSTANCE = new Authentication();

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return AUTHENTICATION_INSTANCE;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{
                AuthenticationExecutionModel.Requirement.REQUIRED,
                AuthenticationExecutionModel.Requirement.DISABLED
        };
    }

    @Override
    public String getDisplayType() {
        return "Authentication SPI";
    }

    @Override
    public String getReferenceCategory() {
        return "";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "This is an Authentication SPI";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        final List<ProviderConfigProperty> configProperties = new ArrayList<>();

        ProviderConfigProperty providerConfigProperty = new ProviderConfigProperty();
        providerConfigProperty.setName(Authentication.CONFIG_ENABLE);
        providerConfigProperty.setLabel("Should access the conditional SPI ?");
        providerConfigProperty.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        providerConfigProperty.setHelpText("Config to access (or not) the conditional spi");

        configProperties.add(providerConfigProperty);

        return configProperties;
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
        return Authentication.ID;
    }
}
