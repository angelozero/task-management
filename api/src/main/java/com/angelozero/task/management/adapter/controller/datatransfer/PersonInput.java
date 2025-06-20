package com.angelozero.task.management.adapter.controller.datatransfer;

import java.util.List;

public record PersonInput(String name, String email, String profileInfo, List<TaskInput> taskList) {
}
