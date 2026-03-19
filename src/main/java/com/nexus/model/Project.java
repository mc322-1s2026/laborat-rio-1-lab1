package com.nexus.model;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private String name;
    private List<Task> tarefas;
    private int totalBudget;

    public Project(String name, int totalBudget) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        this.name = name;
     	this.totalBudget = totalBudget;
        this.tarefas = new ArrayList<>();
    }

    public void addTask(Task t) {
        int total = this.tarefas.stream()
	    .mapToInt(Task::getHoras)
	    .sum();

    	total += t.getHoras();

	    if (total > this.totalBudget) {
            Task.totalValidationErrors++;
            throw new NexusValidationException("Falha ao adicionar tarefa: a soma das horas das tarefas atuais excede as horas totais do projeto.");	
	    }

        this.tarefas.add(t);
    }

    // Getters
    public String getName() { return name; }
    public List<Task> getTarefas() { return tarefas; }
    public int getTotalBudget() { return totalBudget; }

}
