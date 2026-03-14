package com.nexus.model;

public class User {
    private final String username;
    private final String email;

    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }
        this.username = username;
        if (email == null || !email.contains("@dominio.com")) {
            throw new IllegalArgumentException("Email inválido.");
        }
        this.email = email;
    }

    public String consultEmail() {
        return email;
    }

    public String consultUsername() {
        return username;
    }

    public long calculateWorkload(List<Task> allTasks) {
        if (allTasks == null || allTasks.isEmpty()) return 0;

        return allTasks.stream()
            .filter(task -> task.getOwner() != null && task.getOwner().consultUsername().equals(this.username))
            .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
            .count();

    }
}