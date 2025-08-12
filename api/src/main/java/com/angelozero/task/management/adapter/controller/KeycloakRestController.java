package com.angelozero.task.management.adapter.controller;


import com.angelozero.task.management.adapter.controller.mapper.KeycloakRequestMapper;
import com.angelozero.task.management.adapter.controller.rest.request.TokenRequest;
import com.angelozero.task.management.adapter.controller.rest.response.TokenResponse;
import com.angelozero.task.management.usecase.services.token.GenerateTokenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/keycloak")
@RequiredArgsConstructor
public class KeycloakRestController {

    private final GenerateTokenUseCase generateTokenUseCase;
    private final KeycloakRequestMapper keycloakRequestMapper;

    @GetMapping("/user")
//    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<String> helloKeycloakRestApi() {
        return ResponseEntity.ok("Hello Keycloak Rest API for USER !");
    }

    @GetMapping("/admin")
//    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<String> helloKeycloakRestApiAdmin() {
        return ResponseEntity.ok("Hello Keycloak Rest API for ADMIN!");
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> getToken(@RequestBody TokenRequest tokenRequest) {
        var response = generateTokenUseCase.execute(tokenRequest.username(), tokenRequest.password());
        return ResponseEntity.ok(keycloakRequestMapper.toResponse(response));
    }
}
