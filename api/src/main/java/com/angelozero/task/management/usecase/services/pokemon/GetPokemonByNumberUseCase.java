package com.angelozero.task.management.usecase.services.pokemon;

import com.angelozero.task.management.entity.Pokemon;
import com.angelozero.task.management.usecase.exception.BusinessException;
import com.angelozero.task.management.usecase.gateway.PokemonGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetPokemonByNumberUseCase {

    private final PokemonGateway pokemonGateway;

    public Pokemon execute(Integer number) {
        if (number == null) {
            log.error("No Pokemon number was informed to be found");
            throw new BusinessException("No Pokemon number was informed to be found");
        }

        log.info("Getting a Pokemon by number: {}", number);

        var pokemon = pokemonGateway.findByNumber(number);

        if (pokemon == null) {
            log.info("The Pokemon with number {} was not found", number);
            return null;
        }

        log.info("The Pokemon with number {} was found with success", number);
        return pokemon;
    }
}
