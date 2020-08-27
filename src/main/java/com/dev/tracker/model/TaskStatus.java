package com.dev.tracker.model;

public enum TaskStatus {
    VIEW("View"), IN_PROGRESS("In Progress"), DONE("Done");

    private final String name;

    private TaskStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
