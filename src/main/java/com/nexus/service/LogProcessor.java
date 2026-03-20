package com.nexus.service;

import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class LogProcessor {

    public void processLog(String fileName, Workspace workspace) {
        try {
            // Busca o arquivo dentro da pasta de recursos do projeto (target/classes)
            var resource = getClass().getClassLoader().getResourceAsStream(fileName);
            
            if (resource == null) {
                throw new IOException("Arquivo não encontrado no classpath: " + fileName);
            }

            try (java.util.Scanner s = new java.util.Scanner(resource).useDelimiter("\\A")) {
                String content = s.hasNext() ? s.next() : "";
                List<String> lines = List.of(content.split("\\R"));
                
                for (String line : lines) {
                    if (line.isBlank() || line.startsWith("#")) continue;

                    String[] p = line.split(";");
                    String action = p[0];

                    try {
                        switch (action) {
                            case "CREATE_USER" -> {
                                workspace.addUser(new User(p[1], p[2]));
                                System.out.println("[LOG] Usuário criado: " + p[1]);
                            }

                            case "CREATE_PROJECT" -> {
                                workspace.addProject(new Project(p[1], Integer.parseInt(p[2])));
                                System.out.println("[LOG] Projeto criado: " + p[1]);

                            }

                            case "CREATE_TASK" -> {
                                if (p.length == 3) {
                                    Task t = new Task(p[1], LocalDate.parse(p[2]));
                                    workspace.addTask(t);
                                    System.out.println("[LOG] Tarefa criada " + p[1]);
                                }

                                else if (p.length == 5) {
                                    Task t = new Task(p[1], LocalDate.parse(p[2]), Integer.parseInt(p[3]));
                                    Project proj = findProject(workspace, p[4]);
    
                                    proj.addTask(t);
                                    workspace.addTask(t);
                                    System.out.println("[LOG] Tarefa criada e vinculada ao projeto: " + p[4] + ": " + p[1]);
                                } else {
                                    throw new IllegalArgumentException("Parâmetros insuficientes para CREATE_TASK.");
                                }

                            }
                            case "ASSIGN_USER" -> {
                                Task t = findTask(workspace, Integer.parseInt(p[1]));
                                User u = findUser(workspace, p[2]);
                                t.setOwner(u);
                                System.out.println("[LOG] Tarefa" + p[1] + " atribuída a " + p[2]);

                            }
                            case "CHANGE_STATUS" -> {
                                Task t = findTask(workspace, Integer.parseInt(p[1]));
                                TaskStatus newStatus = TaskStatus.valueOf(p[2]);
                                 
                                if (newStatus == TaskStatus.IN_PROGRESS) {
                                    t.moveToInProgress(t.getOwner());
                                } else if (newStatus == TaskStatus.DONE) {
                                    t.markAsDone();
                                } else if (newStatus == TaskStatus.BLOCKED) {
                                    t.setBlocked(true);
                                } else if (newStatus == TaskStatus.TO_DO) {
                                    t.setBlocked(false);
                                }
                                System.out.println("[LOG] Tarefa " + p[1] + " movida para " + newStatus);
                            }
                            case "REPORT_STATUS" -> {
                                System.out.println("\n--- RELATÓRIO NEXUS ---");
                                System.out.println("Top Performers: " + workspace.getTopPerformers().stream().map(User::consultUsername).toList());
                                System.out.println("Overloaded Users: " + workspace.getOverloadedUsers().stream().map(User::consultUsername).toList());
                                System.out.println("Gargalo Global (Status): " + workspace.getGlobalBottlenecks());
                                workspace.getProjects().forEach(proj -> 
                                    System.out.printf("Saúde do Projeto '%s': %.2f%%\n", proj.getName(), workspace.getProjectHealth(proj))
                                );
                                System.out.println("-----------------------\n");
                            }
                            default -> System.err.println("[WARN] Ação desconhecida: " + action);
                        }
                    }  catch (NexusValidationException | IllegalArgumentException e) {
                        Task.totalValidationErrors++;
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    } catch (Exception e) {
                        Task.totalValidationErrors++;
                        System.err.println("[ERRO GERAL] Falha no comando '" + line + "': " + e.getMessage());
                    }
                }
            }    
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }

    private Project findProject (Workspace ws, String name) {
        return ws.getProjects().stream()
            .filter(p -> p.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new NexusValidationException("Projeto náo encontrado " + name));
    }

    private Task findTask(Workspace ws, int id) {
        return ws.getTasks().stream()
            .filter(t -> t.getId() == id)
            .findFirst()
            .orElseThrow(() -> new NexusValidationException("Tarefa náo encontrada: ID " + id));
    }

    private User findUser(Workspace ws, String username) {
        return ws.getUsers().stream()
            .filter(u -> u.consultUsername().equals(username))
            .findFirst()
            .orElseThrow(() -> new NexusValidationException("Usuário náo encontrado: " + username));
    }
}