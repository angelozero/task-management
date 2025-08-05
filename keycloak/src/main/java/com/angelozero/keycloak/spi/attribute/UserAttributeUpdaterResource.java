package com.angelozero.keycloak.spi.attribute;

import com.angelozero.keycloak.spi.attribute.request.AttributeUpdateRequest;
import com.angelozero.keycloak.spi.attribute.request.AttributeUpdateResponse;
import com.angelozero.keycloak.spi.attribute.request.AttributeUpdateStatusResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@Path("/")
public class UserAttributeUpdaterResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAttributeUpdaterResource.class);

    private static final String UPDATE_USER_ATTRIBUTES_ROLE = "update-user-attributes-role";
    private static final String GET_USER_ATTRIBUTES_ROLE = "get-user-attributes-role";

    private final KeycloakSession session;
    private final RealmModel realm;

    public UserAttributeUpdaterResource(KeycloakSession session) {
        this.session = session;
        this.realm = session.getContext().getRealm();
    }

    // path: /auth/realms/{realm}/user-attribute-updater/users/{userId}/attribute
    @PUT
    @Path("users/{userName}/attribute")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSingleUserAttribute(@PathParam("userName") String userName, AttributeUpdateRequest request) {
        LOGGER.info("\n");
        LOGGER.info("[UserAttributeUpdaterResource] - @PUT");

        var auth = new AppAuthManager.BearerTokenAuthenticator(session)
                .setRealm(realm)
                .setUriInfo(session.getContext().getUri())
                .setConnection(session.getContext().getConnection())
                .setHeaders(session.getContext().getRequestHeaders())
                .authenticate();

        if (isValidAuthentication(auth)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Collections.singletonMap("error", "Not authenticated."))
                    .build();
        }

        if (isValidRealmRole(auth, UPDATE_USER_ATTRIBUTES_ROLE)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Collections.singletonMap("error", "Not authorized."))
                    .build();
        }

        if (isValidRequestData(request)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Collections.singletonMap("error", "Attribute name and value are required."))
                    .build();
        }

        var user = session.users().getUserByUsername(realm, userName);

        if (isValidUser(user)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Collections.singletonMap("error", "User not found."))
                    .build();
        }

        user.setSingleAttribute(request.attributeName(), request.attributeValue());

        var message = String.format("Attribute '%s' with value '%s' was updated for user '%s' with success.",
                request.attributeName(), request.attributeValue(), user.getUsername());

        var response = new AttributeUpdateStatusResponse("success", message);

        LOGGER.info("[UserAttributeUpdaterResource] - PUT Request success - Response {}", response);
        return Response.ok(response).build();
    }

    @GET
    @Path("users/{userName}/attributes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserAttribute(@PathParam("userName") String userName) {
        LOGGER.info("\n");
        LOGGER.info("[UserAttributeUpdaterResource] - @GET");

        var auth = new AppAuthManager.BearerTokenAuthenticator(session)
                .setRealm(realm)
                .setUriInfo(session.getContext().getUri())
                .setConnection(session.getContext().getConnection())
                .setHeaders(session.getContext().getRequestHeaders())
                .authenticate();

        if (isValidAuthentication(auth)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Collections.singletonMap("error", "Not authenticated."))
                    .build();
        }

        if (isValidRealmRole(auth, GET_USER_ATTRIBUTES_ROLE)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Collections.singletonMap("error", "Not authorized."))
                    .build();
        }

        UserModel user = session.users().getUserByUsername(realm, userName);

        if (isValidUser(user)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Collections.singletonMap("error", "User not found."))
                    .build();
        }

        var attributes = user.getAttributes();

        var response = new AttributeUpdateResponse(attributes);

        LOGGER.info("[UserAttributeUpdaterResource] - GET Request success - Response {}", response);
        return Response.ok(response).build();
    }

    private static boolean isValidUser(UserModel user) {
        if (user == null) {
            LOGGER.error("[UserAttributeUpdaterResource] - User not found.");
            return true;
        } else {
            LOGGER.info("[UserAttributeUpdaterResource] - User found.");
        }
        return false;
    }

    private static boolean isValidRequestData(AttributeUpdateRequest request) {
        if (request.attributeName() == null || request.attributeName().isEmpty() || request.attributeValue() == null) {
            LOGGER.error("[UserAttributeUpdaterResource] - Attribute name and value are required.");
            return true;
        } else {
            LOGGER.info("[UserAttributeUpdaterResource] - Request data {}", request);
        }
        return false;
    }

    private static boolean isValidRealmRole(AuthenticationManager.AuthResult auth, String clientRole) {
        if (auth.getUser().getRealmRoleMappingsStream().noneMatch(role -> clientRole.equals(role.getName()))) {
            LOGGER.error("[UserAttributeUpdaterResource] - Client not authorized to update user attributes.");
            LOGGER.error("[UserAttributeUpdaterResource] - Missing role: {}", clientRole);
            return true;
        } else {
            LOGGER.info("[UserAttributeUpdaterResource] - Client authorized to update user attributes.");
        }
        return false;
    }

    private static boolean isValidAuthentication(AuthenticationManager.AuthResult auth) {
        if (auth == null) {
            LOGGER.error("[UserAttributeUpdaterResource] - User is not authenticated");
            return true;
        } else {
            LOGGER.info("[UserAttributeUpdaterResource] - User is authenticated");
        }
        return false;
    }
}
