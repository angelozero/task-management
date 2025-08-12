package com.angelozero.task.management.adapter.dataprovider.mapper;

import com.angelozero.task.management.adapter.dataprovider.rest.response.keycloak.KeycloakTokenResponse;
import com.angelozero.task.management.entity.Token;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KeycloakDataProviderMapper {

    Token toToken(KeycloakTokenResponse response);
}
