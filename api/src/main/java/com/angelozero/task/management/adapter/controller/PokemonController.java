package com.angelozero.task.management.adapter.controller;

import com.angelozero.task.management.adapter.controller.mapper.PokemonRequestMapper;
import com.angelozero.task.management.adapter.controller.rest.response.PokemonResponse;
import com.angelozero.task.management.usecase.services.pokemon.GetPokemonByNameUseCase;
import com.angelozero.task.management.usecase.services.pokemon.GetPokemonByNumberUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/pokemon")
@AllArgsConstructor
public class PokemonController {

    private final GetPokemonByNameUseCase getPokemonByNameUseCase;
    private final GetPokemonByNumberUseCase getPokemonByNumberUseCase;

    private final PokemonRequestMapper pokemonRequestMapper;


    @GetMapping("/{name}")
    public ResponseEntity<PokemonResponse> getPokemonByName(@PathVariable String name) {
        var pokemon = getPokemonByNameUseCase.execute(name);
        var pokemonResponse = pokemonRequestMapper.toPokemonResponse(pokemon);

        return Optional.ofNullable(pokemonResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
