package com.angelozero.task.management.entity.unit.usecase.pokemon;

import com.angelozero.task.management.entity.Pokemon;
import com.angelozero.task.management.usecase.gateway.CacheGateway;
import com.angelozero.task.management.usecase.gateway.PokemonCachePropertiesGateway;
import com.angelozero.task.management.usecase.gateway.PokemonGateway;
import com.angelozero.task.management.usecase.services.pokemon.GetPokemonByNameUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetPokemonByNameUseCaseUseCaseTest {

    @Mock
    private PokemonGateway pokemonGateway;

    @Mock
    private CacheGateway cacheGateway;

    @Mock
    private PokemonCachePropertiesGateway cachePropertiesGateway;

    @InjectMocks
    private GetPokemonByNameUseCase getPokemonByNameUseCase;

    @Test
    @DisplayName("Should find a pokemon by name with success")
    public void shouldFindPokemonByNameWithSuccess() {
        var pokemonMock = new Pokemon(0, "test", "teste");

        when(cachePropertiesGateway.getKey()).thenReturn("key");
        when(cachePropertiesGateway.getTtl()).thenReturn(1);
        when(cacheGateway.get(any(), any())).thenReturn(null);
        when(pokemonGateway.findByName(anyString())).thenReturn(pokemonMock);

        var response = getPokemonByNameUseCase.execute("test");

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should not find a pokemon by name")
    public void shouldNotFindPokemonByName() {
        when(cachePropertiesGateway.getKey()).thenReturn("key");
        when(cacheGateway.get(any(), any())).thenReturn(null);
        when(pokemonGateway.findByName(anyString())).thenReturn(null);

        var response = getPokemonByNameUseCase.execute("test");

        assertNull(response);
    }
}
