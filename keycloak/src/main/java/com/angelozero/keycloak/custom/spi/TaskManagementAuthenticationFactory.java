package com.angelozero.keycloak.custom.spi;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

public class TaskManagementAuthenticationFactory implements AuthenticatorFactory {

    private static final TaskManagementAuthenticator CUSTOM_AUTHENTICATOR_INSTANCE = new TaskManagementAuthenticator();

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return CUSTOM_AUTHENTICATOR_INSTANCE;
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
        return "Task Management Authentication";
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
        return "Simple authentication to access Task Management API";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        final List<ProviderConfigProperty> configProperties = new ArrayList<>();

        ProviderConfigProperty clientMasterEnableConfig = new ProviderConfigProperty();
        clientMasterEnableConfig.setName(TaskManagementAuthenticator.TASK_MANAGEMENT_CONFIG_ENABLE);
        clientMasterEnableConfig.setLabel("Is enable ?");
        clientMasterEnableConfig.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        clientMasterEnableConfig.setHelpText("This is a simple component");

        configProperties.add(clientMasterEnableConfig);

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
        return TaskManagementAuthenticator.TASK_MANAGEMENT_AUTHENTICATOR_PROVIDER_ID;
    }
}
