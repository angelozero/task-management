package com.angelozero.task.management.adapter.dataprovider.jpa.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public record PersonEntity(@Id String id,
                           String name,
                           String email,
                           String profileInfo,
                           List<String> taskIdsList) {
}
