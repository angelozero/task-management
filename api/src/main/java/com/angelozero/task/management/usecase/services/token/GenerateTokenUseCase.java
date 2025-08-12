package com.angelozero.task.management.usecase.services.token;

import com.angelozero.task.management.entity.Token;
import com.angelozero.task.management.usecase.gateway.TokenGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateTokenUseCase {

    private final TokenGateway tokenGateway;

    public Token execute(String username, String password) {
        log.info("Generating token");

        var token = tokenGateway.generateToken(username, password);

        log.info("Token generated with success - Access Token: {}", token.accessToken());
        return token;
    }
}
