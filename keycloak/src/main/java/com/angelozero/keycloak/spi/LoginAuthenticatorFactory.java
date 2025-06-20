package com.angelozero.keycloak.spi;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

public class LoginAuthenticatorFactory implements AuthenticatorFactory {

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return new LoginAuthenticator();
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
        return "Login Authenticator SPI";
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
        return true;
    }

    @Override
    public String getHelpText() {
        return "Simple SPI to log user data in login";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        final List<ProviderConfigProperty> configProperties = new ArrayList<>();

        ProviderConfigProperty clientMasterConfig = new ProviderConfigProperty();
        clientMasterConfig.setName("login_config_string_value");
        clientMasterConfig.setLabel("Login Config String Value");
        clientMasterConfig.setType(ProviderConfigProperty.STRING_TYPE);
        clientMasterConfig.setHelpText("Simple help text");

        ProviderConfigProperty clientMasterEnableConfig = new ProviderConfigProperty();
        clientMasterEnableConfig.setName("login_config_boolean_value");
        clientMasterEnableConfig.setLabel("Login Config Boolean Value");
        clientMasterEnableConfig.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        clientMasterEnableConfig.setHelpText("Simple help text");

        configProperties.add(clientMasterConfig);
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
        return LoginAuthenticator.LOGIN_AUTHENTICATOR_ID;
    }
}
