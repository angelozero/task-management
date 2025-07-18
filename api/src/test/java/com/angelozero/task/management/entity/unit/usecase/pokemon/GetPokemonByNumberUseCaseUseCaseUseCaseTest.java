package com.angelozero.task.management.entity.unit.usecase.pokemon;

import com.angelozero.task.management.entity.Pokemon;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.services.pokemon.GetPokemonByNumberUseCase;
import com.angelozero.task.management.usecase.gateway.PokemonGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetPokemonByNumberUseCaseUseCaseUseCaseTest {

    @Mock
    private PokemonGateway pokemonGateway;

    @InjectMocks
    private GetPokemonByNumberUseCase getPokemonByNumberUseCase;

    @Test
    @DisplayName("Should find a pokemon by number with success")
    public void shouldFindPokemonByNumberWithSuccess() {
        var pokemonMock = new Pokemon(0, "test", "teste");

        when(pokemonGateway.findByNumber(anyInt())).thenReturn(pokemonMock);

        var response = getPokemonByNumberUseCase.execute(0);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should not find a pokemon by number")
    public void shouldNotFindPokemonByNumber() {
        when(pokemonGateway.findByNumber(anyInt())).thenReturn(null);

        var response = getPokemonByNumberUseCase.execute(0);

        assertNull(response);
    }

    @Test
    @DisplayName("Should fail to find a pokemon by number")
    public void shouldFailFindPokemonByNumber() {

        var exception = assertThrows(BusinessException.class, () -> getPokemonByNumberUseCase.execute(null));

        assertNotNull(exception);
        assertEquals("No Pokemon number was informed to be found", exception.getMessage());

        verify(pokemonGateway, never()).findByNumber(anyInt());
    }
}
