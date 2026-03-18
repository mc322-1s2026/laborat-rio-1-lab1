package com.nexus.model;

public class Project {
    private String name;
    private List<Task> tarefas;
    private int totalBudget;

    public Project(String name, List<Task> tarefas, int totalBudget) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        this.name = name;
        this.tarefas = tarefas;
	this.totalBudget = totalBudget;
    }

    public void addTask(Task t) {
        int total = this.tarefas.stream()
	    .mapToInt(Task::getHoras);
	    .sum();
	total += t.getHoras();

	if (total > this.totalBudget) {
            throw new NexusValidationException("Falha ao adicionar tarefa: a soma das horas das tarefas atuais excede as horas totais do projeto.");	
	}
    }

}
