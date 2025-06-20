package com.angelozero.task.management.entity;

import java.util.List;

public record Person(String id, String name, String email, String profileInfo, List<Task> taskList) {
}
