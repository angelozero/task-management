package com.angelozero.task.management.usecase.services.pokemon;

import com.angelozero.task.management.entity.Pokemon;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.CacheGateway;
import com.angelozero.task.management.usecase.gateway.PokemonCachePropertiesGateway;
import com.angelozero.task.management.usecase.gateway.PokemonGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetPokemonByNameUseCase {

    private final PokemonGateway pokemonGateway;
    private final CacheGateway cacheGateway;
    private final PokemonCachePropertiesGateway cachePropertiesGateway;

    public Pokemon execute(String name) {
        if (StringUtils.isBlank(name)) {
            log.error("No Pokemon name was informed to be found");
            throw new BusinessException("No Pokemon name was informed to be found");
        }

        var cachedPokemon = cacheGateway.get(cachePropertiesGateway.getKey(), Pokemon.class);

        if (cachedPokemon != null) {
            log.info("Getting a Pokemon by cache - Pokemon: {}", name);
            return cachedPokemon;
        }

        log.info("Getting a Pokemon by name: {}", name);

        var pokemon = pokemonGateway.findByName(name);

        if (pokemon == null) {
            log.info("The Pokemon {} was not found", name);
            return null;
        }

        log.info("The Pokemon {} was found with success", name);
        saveCache(pokemon);
        return pokemon;
    }

    private void saveCache(Pokemon pokemon) {
        log.info("Saving Pokemon {} in cache data", pokemon.name());
        cacheGateway.save(cachePropertiesGateway.getKey(), pokemon, cachePropertiesGateway.getTtl());
    }
}
