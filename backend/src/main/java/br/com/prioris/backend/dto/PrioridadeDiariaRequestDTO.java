package br.com.prioris.backend.dto;

import jakarta.validation.constraints.NotNull;

public class PrioridadeDiariaRequestDTO {

    @NotNull(message = "A tarefa é obrigatória")
    private Long idTarefa;

    public Long getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(Long idTarefa) {
        this.idTarefa = idTarefa;
    }
}