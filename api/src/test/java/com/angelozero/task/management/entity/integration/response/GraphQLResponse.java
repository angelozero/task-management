package com.angelozero.task.management.entity.integration.response;

import com.angelozero.task.management.adapter.controller.datatransfer.PersonInput;
import com.angelozero.task.management.adapter.controller.datatransfer.PersonOutput;
import lombok.Getter;

@Getter
public class GraphQLResponse {
    private Data data;

    @Getter
    public static class Data {
        private PersonOutput personByEmail;
        private PersonOutput personById;
        private PersonOutput savePerson;
    }
}