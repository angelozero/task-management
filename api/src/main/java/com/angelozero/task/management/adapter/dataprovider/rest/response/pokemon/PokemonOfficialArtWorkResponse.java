package com.angelozero.task.management.adapter.dataprovider.rest.response.pokemon;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PokemonOfficialArtWorkResponse(@JsonProperty("front_default") String artWork) {
}
