package com.angelozero.task.management.adapter.controller.mapper;

import com.angelozero.task.management.adapter.controller.rest.response.PokemonResponse;
import com.angelozero.task.management.entity.Pokemon;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PokemonRequestMapper {

    PokemonResponse toPokemonResponse(Pokemon pokemon);
}
