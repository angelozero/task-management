package com.angelozero.keycloak.spi.attribute.request;

import java.util.List;
import java.util.Map;

public record AttributeUpdateResponse(Map<String, List<String>> attributeValues) {
}
