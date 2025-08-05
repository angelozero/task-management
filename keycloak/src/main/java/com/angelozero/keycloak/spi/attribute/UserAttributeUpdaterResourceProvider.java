package com.angelozero.keycloak.spi.attribute;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public class UserAttributeUpdaterResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    public UserAttributeUpdaterResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return new UserAttributeUpdaterResource(session);
    }

    @Override
    public void close() {

    }
}
