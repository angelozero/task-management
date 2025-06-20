package com.angelozero.task.management.adapter.dataprovider.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PokemonResponse(@JsonProperty("name")
                              String name,
                              @JsonProperty("order")
                              Integer number,
                              @JsonProperty("sprites")
                              PokemonSpritesResponse pokemonSpritesResponse) {
}
