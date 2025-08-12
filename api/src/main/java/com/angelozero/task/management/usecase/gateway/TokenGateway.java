package com.angelozero.task.management.usecase.gateway;

import com.angelozero.task.management.entity.Token;

public interface TokenGateway {
    Token generateToken(String username, String password);
}
