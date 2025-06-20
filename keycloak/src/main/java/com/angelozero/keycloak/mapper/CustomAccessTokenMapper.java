package com.angelozero.keycloak.mapper;


import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CustomAccessTokenMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAccessTokenMapper.class);

    public static final String CUSTOM_ACCESS_TOKEN_MAPPER_ID = "custom-access-token-map-id";


    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, CustomAccessTokenMapper.class);
    }

    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession, KeycloakSession keycloakSession, ClientSessionContext clientSessionCtx) {
        LOGGER.info("[CustomAccessTokenMapper] - Custom Access Token Mapper SPI");

        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, List.of(
                new AccessTokenDTO("Angelo"),
                new AccessTokenDTO("Zero"),
                new AccessTokenDTO("Custom"),
                new AccessTokenDTO("Mapper"),
                new AccessTokenDTO("SPI")
        ));

        LOGGER.info("[CustomAccessTokenMapper] - Token updated with object list - success");
    }

    @Override
    public AccessToken transformAccessToken(AccessToken token, ProtocolMapperModel mappingModel, KeycloakSession session, UserSessionModel userSession, ClientSessionContext clientSessionCtx) {
        LOGGER.info("[CustomAccessTokenMapper] - transformAccessToken accept only a list of string");

        token.getOtherClaims().put("string_list_value", List.of(
                "Angelo",
                "Zero",
                "Custom",
                "Mapper",
                "SPI"));

        setClaim(token, mappingModel, userSession, session, clientSessionCtx);

        LOGGER.info("[CustomAccessTokenMapper] - Token updated with string list - success");
        return token;
    }

    @Override
    public String getDisplayCategory() {
        return "AngeloZero - Custom Access Token Mapper";
    }

    @Override
    public String getDisplayType() {
        return "AngeloZero - Custom Access Token Mapper";
    }

    @Override
    public String getHelpText() {
        return "This is a custom access token mapper SPI";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return CUSTOM_ACCESS_TOKEN_MAPPER_ID;
    }

}
