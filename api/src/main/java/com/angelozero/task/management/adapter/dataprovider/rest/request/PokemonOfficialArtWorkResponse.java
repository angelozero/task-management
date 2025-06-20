package com.angelozero.task.management.adapter.dataprovider.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PokemonOfficialArtWorkResponse(@JsonProperty("front_default") String artWork) {
}
