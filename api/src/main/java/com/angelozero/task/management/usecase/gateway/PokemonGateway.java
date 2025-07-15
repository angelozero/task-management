package com.angelozero.task.management.usecase.gateway;

import com.angelozero.task.management.entity.Pokemon;

public interface PokemonGateway {

    Pokemon findByName(String name);

    Pokemon findByNumber(Integer number);

    Pokemon findByRandomValue();
}
