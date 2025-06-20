package com.angelozero.task.management.adapter.dataprovider.jpa.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record TaskEntity(@Id String id,
                         String description,
                         boolean completed,
                         String statusType,
                         int statusCode) {
}
