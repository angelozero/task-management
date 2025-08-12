package com.angelozero.task.management.adapter.dataprovider.rest.response.pokemon;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PokemonOtherResponse(@JsonProperty("official-artwork")
                                   PokemonOfficialArtWorkResponse pokemonOfficialArtWorkResponse) {
}
