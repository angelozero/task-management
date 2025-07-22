package com.angelozero.task.management.entity.unit.adapter.controller;

import com.angelozero.task.management.adapter.controller.PokemonController;
import com.angelozero.task.management.adapter.controller.mapper.PokemonRequestMapper;
import com.angelozero.task.management.adapter.controller.rest.response.PokemonResponse;
import com.angelozero.task.management.entity.Pokemon;
import com.angelozero.task.management.usecase.services.pokemon.GetPokemonByNameUseCase;
import com.angelozero.task.management.usecase.services.pokemon.GetPokemonByNumberUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PokemonControllerTest {

    @Mock
    private GetPokemonByNameUseCase getPokemonByNameUseCase;

    @Mock
    private GetPokemonByNumberUseCase getPokemonByNumberUseCase;

    @Mock
    private PokemonRequestMapper pokemonRequestMapper;

    @InjectMocks
    private PokemonController pokemonController;

    @Test
    @DisplayName("Should get pokemon by name with success")
    public void shouldGetPokemonByNameWithSuccess() {
        var name = "Pikachu";
        var pokemon = new Pokemon(25, "Pikachu", "artWork");
        var pokemonResponse = new PokemonResponse(25, "Pikachu", "artWork");

        when(getPokemonByNameUseCase.execute(name)).thenReturn(pokemon);
        when(pokemonRequestMapper.toPokemonResponse(pokemon)).thenReturn(pokemonResponse);

        var response = pokemonController.getPokemonByName(name);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pokemonResponse, response.getBody());
    }

    @Test
    @DisplayName("Should return no content when pokemon name not found")
    public void shouldReturnNoContentWhenPokemonNameNotFound() {
        var name = "NonExistentPokemon";

        when(getPokemonByNameUseCase.execute(name)).thenReturn(null);
        when(pokemonRequestMapper.toPokemonResponse(null)).thenReturn(null);

        var response = pokemonController.getPokemonByName(name);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}