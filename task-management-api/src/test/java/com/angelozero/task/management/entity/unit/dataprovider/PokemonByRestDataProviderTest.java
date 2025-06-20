package com.angelozero.task.management.entity.unit.dataprovider;

import com.angelozero.task.management.adapter.dataprovider.PokemonByRestDataProvider;
import com.angelozero.task.management.adapter.dataprovider.mapper.PokemonDataProviderMapper;
import com.angelozero.task.management.adapter.dataprovider.rest.PokemonApiFeignClient;
import com.angelozero.task.management.adapter.dataprovider.rest.request.PokemonResponse;
import com.angelozero.task.management.entity.Pokemon;
import com.angelozero.task.management.usecase.exception.RestDataProviderException;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PokemonByRestDataProviderTest {

    @Mock
    private PokemonApiFeignClient pokemonApiFeignClient;

    @Mock
    private PokemonDataProviderMapper pokemonDataProviderMapper;

    @InjectMocks
    private PokemonByRestDataProvider pokemonByRestDataProvider;

    @Test
    @DisplayName("Should call Pokemon API by Name with success")
    void shouldCallPokemonAPIByNameWithSuccess() {
        var pokemonResponseMock = new PokemonResponse(null, null, null);
        var pokemonMock = new Pokemon(0, "test", "test");

        when(pokemonApiFeignClient.getPokemonByName(anyString())).thenReturn(pokemonResponseMock);
        when(pokemonDataProviderMapper.toPokemon(any(PokemonResponse.class))).thenReturn(pokemonMock);

        var response = pokemonByRestDataProvider.findByName("test");

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should fail to call Pokemon API by Name - FeignClientException")
    void shouldFailToCallPokemonAPIByNameFeignClientException() {
        var errorMessage = "Fail to call Pokemon API - Error Message: [404 Internal Server Error] during [GET] to [http://example.com] [getData]: [] - Status Code: 404";
        var feignClientExceptionMock = FeignException.errorStatus("getData",
                Response.builder()
                        .status(404)
                        .reason("Internal Server Error")
                        .request(Request.create(Request.HttpMethod.GET, "http://example.com",
                                new HashMap<>(), null, null, null))
                        .build());

        when(pokemonApiFeignClient.getPokemonByName(anyString())).thenThrow(feignClientExceptionMock);

        var exception = assertThrows(RestDataProviderException.class,
                () -> pokemonByRestDataProvider.findByName("test"));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(pokemonDataProviderMapper, never()).toPokemon(any(PokemonResponse.class));
    }

    @Test
    @DisplayName("Should fail to call Pokemon API by Name - Exception")
    void shouldFailToCallPokemonAPIByNameException() {
        var errorMessage = "Fail to call Pokemon API - Error Message: error-test";

        when(pokemonApiFeignClient.getPokemonByName(anyString())).thenThrow(new RuntimeException("error-test"));

        var exception = assertThrows(RestDataProviderException.class,
                () -> pokemonByRestDataProvider.findByName("test"));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(pokemonDataProviderMapper, never()).toPokemon(any(PokemonResponse.class));
    }

    @Test
    @DisplayName("Should call Pokemon API by Number with success")
    void shouldCallPokemonAPIByNumberWithSuccess() {
        var pokemonResponseMock = new PokemonResponse(null, null, null);
        var pokemonMock = new Pokemon(0, "test", "test");

        when(pokemonApiFeignClient.getPokemonByNumber(anyInt())).thenReturn(pokemonResponseMock);
        when(pokemonDataProviderMapper.toPokemon(any(PokemonResponse.class))).thenReturn(pokemonMock);

        var response = pokemonByRestDataProvider.findByNumber(0);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should fail to call Pokemon API by Number - FeignClientException")
    void shouldFailToCallPokemonAPIByNumberFeignClientException() {
        var errorMessage = "Fail to call Pokemon API - Error Message: [404 Internal Server Error] during [GET] to [http://example.com] [getData]: [] - Status Code: 404";
        var feignClientExceptionMock = FeignException.errorStatus("getData",
                Response.builder()
                        .status(404)
                        .reason("Internal Server Error")
                        .request(Request.create(Request.HttpMethod.GET, "http://example.com",
                                new HashMap<>(), null, null, null))
                        .build());

        when(pokemonApiFeignClient.getPokemonByNumber(anyInt())).thenThrow(feignClientExceptionMock);

        var exception = assertThrows(RestDataProviderException.class,
                () -> pokemonByRestDataProvider.findByNumber(0));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(pokemonDataProviderMapper, never()).toPokemon(any(PokemonResponse.class));
    }

    @Test
    @DisplayName("Should fail to call Pokemon API by Number - Exception")
    void shouldFailToCallPokemonAPIByNumberException() {
        var errorMessage = "Fail to call Pokemon API - Error Message: error-test";

        when(pokemonApiFeignClient.getPokemonByNumber(anyInt())).thenThrow(new RuntimeException("error-test"));

        var exception = assertThrows(RestDataProviderException.class,
                () -> pokemonByRestDataProvider.findByNumber(0));

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());

        verify(pokemonDataProviderMapper, never()).toPokemon(any(PokemonResponse.class));
    }
}
