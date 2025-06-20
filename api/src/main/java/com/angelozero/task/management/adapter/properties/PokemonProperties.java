package com.angelozero.task.management.adapter.properties;

import com.angelozero.task.management.usecase.gateway.PokemonCachePropertiesGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PokemonProperties extends CacheProperties implements PokemonCachePropertiesGateway {

    @Override
    @Value("${cache.pokemon.key}")
    public void setKey(String key) {
        super.setKey(key);
    }

    @Override
    @Value("${cache.pokemon.ttl}")
    public void setTtl(Integer ttl) {
        super.setTtl(ttl);
    }
}
