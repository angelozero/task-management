package com.angelozero.keycloak.mapper;

import com.angelozero.keycloak.mapper.dto.Data;
import com.angelozero.keycloak.mapper.dto.InfoData;
import com.angelozero.keycloak.mapper.dto.InfoDataList;
import com.angelozero.keycloak.spi.auth.Authentication;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccessTokenMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenMapper.class);

    private static final String ACCESS_TOKEN_MAPPER_ID = "access-token-mapper-id";


    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, AccessTokenMapper.class);
    }

    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession, KeycloakSession keycloakSession, ClientSessionContext clientSessionCtx) {
        LOGGER.info("\n");
        LOGGER.info("[AccessTokenMapper] - Set Claim");
        var userName = clientSessionCtx.getClientSession().getUserSession().getUser().getFirstName();
        var infoDataList = generateInfoDataList();
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, infoDataList);
        LOGGER.info("[AccessTokenMapper] - Token updated with success for the user {}", userName);
    }


    @Override
    public AccessToken transformAccessToken(AccessToken token, ProtocolMapperModel mappingModel, KeycloakSession session, UserSessionModel userSession, ClientSessionContext clientSessionCtx) {
        LOGGER.info("\n");
        LOGGER.info("[AccessTokenMapper] - Transform Access Token");
        var isEnable = isAuthenticationSPIConfigEnable(clientSessionCtx);
        var firstName = clientSessionCtx.getClientSession().getUserSession().getUser().getFirstName();

        if (isEnable) {
            LOGGER.info("[AccessTokenMapper] - Transform Access Token config in Authentication SPI is enable");
            var data = getData();
            token.getOtherClaims().put("data_list", data);
            setClaim(token, mappingModel, userSession, session, clientSessionCtx);
            LOGGER.info("[AccessTokenMapper] - The list string \"data_list\" was inserted in token with values for the user {}", firstName);

        } else {
            LOGGER.info("[AccessTokenMapper] - Transform Access Token config in Authentication is not enable");
            token.getOtherClaims().put("data_list", "EMPTY_VALUE");
            setClaim(token, mappingModel, userSession, session, clientSessionCtx);
            LOGGER.info("[AccessTokenMapper] - The list string \"data_list\" was inserted in token without values for the user {}", firstName);
        }

        return token;
    }

    @Override
    public String getDisplayCategory() {
        return "Access Token Mapper";
    }

    @Override
    public String getDisplayType() {
        return "Access Token Mapper";
    }

    @Override
    public String getHelpText() {
        return "This is an Access Token Mapper";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return ACCESS_TOKEN_MAPPER_ID;
    }

    private static Data getData() {
        return new Data(List.of("data-1", "data-2", "data-2"));
    }

    private InfoDataList generateInfoDataList() {
        return new InfoDataList(List.of(new InfoData("info-data-1"), new InfoData("info-data-2"), new InfoData("info-data-3")));
    }

    private Boolean isAuthenticationSPIConfigEnable(ClientSessionContext clientSessionCtx) {

        var realm = clientSessionCtx.getClientSession().getRealm();
        var authenticatorConfigByList = realm.getAuthenticatorConfigsStream().toList();

        return authenticatorConfigByList.stream()
                .filter(Objects::nonNull)
                .map(AuthenticatorConfigModel::getConfig)
                .filter(enable -> Boolean.parseBoolean(enable.get(Authentication.CONFIG_ENABLE_ACCESS_TOKEN_MAPPER)))
                .map(enable -> Boolean.parseBoolean(enable.get(Authentication.CONFIG_ENABLE_ACCESS_TOKEN_MAPPER)))
                .findFirst()
                .orElse(Boolean.FALSE);
    }
}
