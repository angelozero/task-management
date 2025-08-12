package com.angelozero.task.management.usecase.util;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GetRestTemplate {

    public RestTemplate execute() {
        return new RestTemplate();
    }
}
