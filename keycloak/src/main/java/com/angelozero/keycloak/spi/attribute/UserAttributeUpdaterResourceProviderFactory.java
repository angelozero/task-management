package com.angelozero.keycloak.spi.attribute;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class UserAttributeUpdaterResourceProviderFactory implements RealmResourceProviderFactory {

    private static final String ID = "user-attribute-updater";

    @Override
    public RealmResourceProvider create(KeycloakSession keycloakSession) {
        return new UserAttributeUpdaterResourceProvider(keycloakSession);
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return ID;
    }
}
