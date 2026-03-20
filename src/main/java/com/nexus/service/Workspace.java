package com.nexus.service;

import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.Map;


public class Workspace {
    private final List<Task> tasks = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();


    public void addTask(Task task) { tasks.add(task); }
    public void addUser(User user) { users.add(user); }
    public void addProject(Project project) { projects.add(project); }

    public List<Task> getTasks() { return Collections.unmodifiableList(tasks); }
    public List<User> getUsers() { return Collections.unmodifiableList(users); }
    public List<Project> getProjects() { return Collections.unmodifiableList(projects); }

    public List<User> getTopPerformers() {
        return tasks.stream()
            .filter(t-> t.getStatus() == TaskStatus.DONE && t.getOwner() != null)
            .collect(Collectors.groupingBy(Task::getOwner, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
            .limit(3)
            .map(Map.Entry::getKey)
            .toList();
    } 

    public List<User> getOverloadedUsers() {
        return users.stream()
            .filter(u -> u.calculateWorkload(tasks) > 10)
            .toList();
    }

    public double getProjectHealth(Project project) {
        if(project.getTarefas().isEmpty()) return 0.0;

        long doneCount = project.getTarefas().stream()
            .filter(t -> t.getStatus() == TaskStatus.DONE)
            .count();

            return ((double) doneCount / project.getTarefas().size()) * 100;
    }

    public TaskStatus getGlobalBottlenecks() {
        return tasks.stream()
            .filter(t -> t.getStatus() != TaskStatus.DONE)
            .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
}