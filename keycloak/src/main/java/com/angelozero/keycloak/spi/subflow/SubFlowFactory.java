package com.angelozero.keycloak.spi.subflow;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

public class SubFlowFactory implements AuthenticatorFactory {

    public static final String CONFIG_VALUE = "configValue";
    private static final String ID = "sub-flow-id";
    private static final SubFlow SUB_FLOW_INSTANCE = new SubFlow();

    @Override
    public String getDisplayType() {
        return "SubFlow SPI";
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
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{
                AuthenticationExecutionModel.Requirement.REQUIRED,
                AuthenticationExecutionModel.Requirement.DISABLED
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public String getHelpText() {
        return "This is a Sub Flow SPI";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        final List<ProviderConfigProperty> configProperties = new ArrayList<>();

        ProviderConfigProperty providerConfigProperty = new ProviderConfigProperty();
        providerConfigProperty.setName(CONFIG_VALUE);
        providerConfigProperty.setLabel("Type any value here!");
        providerConfigProperty.setType(ProviderConfigProperty.STRING_TYPE);

        configProperties.add(providerConfigProperty);

        return configProperties;
    }

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return SUB_FLOW_INSTANCE;
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
}
