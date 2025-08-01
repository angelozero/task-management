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

    private static final String ID = "authentication-id";
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

        var providerConfigPropertyForConditional = getProviderConfigProperty(Authentication.CONFIG_ENABLE_CONDITIONAL_SPI,
                "Enable config to access Conditional SPI",
                "Config to access (or not) the conditional spi");

        var providerConfigPropertyForTokenMapper = getProviderConfigProperty(Authentication.CONFIG_ENABLE_ACCESS_TOKEN_MAPPER,
                "Enable config to transform the Access Token by Mapper",
                "Config to transform (or not) the the access token");

        configProperties.add(providerConfigPropertyForConditional);
        configProperties.add(providerConfigPropertyForTokenMapper);

        return configProperties;
    }

    private ProviderConfigProperty getProviderConfigProperty(String configEnableAccessTokenMapper, String label, String helpText) {
        ProviderConfigProperty providerConfigPropertyForTokenMapper = new ProviderConfigProperty();
        providerConfigPropertyForTokenMapper.setName(configEnableAccessTokenMapper);
        providerConfigPropertyForTokenMapper.setLabel(label);
        providerConfigPropertyForTokenMapper.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        providerConfigPropertyForTokenMapper.setHelpText(helpText);
        return providerConfigPropertyForTokenMapper;
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
