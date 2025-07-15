package com.angelozero.task.management.adapter.controller.datatransfer;

import java.util.List;

public record PersonOutput(String id, String name, String email, String profileInfo, List<TaskOutput> taskList) {
}
