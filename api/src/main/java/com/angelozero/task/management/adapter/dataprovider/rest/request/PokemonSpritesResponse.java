package com.angelozero.task.management.adapter.dataprovider.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PokemonSpritesResponse(@JsonProperty("other")
                                     PokemonOtherResponse pokemonOtherResponse) {
}
