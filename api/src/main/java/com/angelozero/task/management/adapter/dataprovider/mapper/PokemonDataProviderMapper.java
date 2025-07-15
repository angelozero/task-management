package com.angelozero.task.management.adapter.dataprovider.mapper;

import com.angelozero.task.management.adapter.dataprovider.rest.request.PokemonResponse;
import com.angelozero.task.management.entity.Pokemon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PokemonDataProviderMapper {

    @Mapping(source = "number", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "pokemonSpritesResponse.pokemonOtherResponse.pokemonOfficialArtWorkResponse.artWork", target = "artWork")
    Pokemon toPokemon(PokemonResponse pokemonResponse);
}