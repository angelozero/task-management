package com.angelozero.task.management.adapter.controller.mapper;

import com.angelozero.task.management.adapter.controller.rest.response.TokenResponse;
import com.angelozero.task.management.entity.Token;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KeycloakRequestMapper {

    TokenResponse toResponse(Token token);
}
