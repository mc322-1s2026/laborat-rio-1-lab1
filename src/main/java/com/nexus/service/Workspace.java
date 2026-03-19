package com.nexus.service;

import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;


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

}