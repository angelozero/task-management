package com.angelozero.task.management.adapter.dataprovider.rest;

import com.angelozero.task.management.adapter.dataprovider.rest.request.PokemonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pokemon-api", url = "${pokemon.api.url}")
public interface PokemonApiFeignClient {

    @GetMapping("${pokemon.api.path}")
    PokemonResponse getPokemonByName(@PathVariable("value") String name);

    @GetMapping("${pokemon.api.path}")
    PokemonResponse getPokemonByNumber(@PathVariable("value") Integer id);
}
