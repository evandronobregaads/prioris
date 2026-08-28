package br.com.prioris.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CicloRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    @Size(
            max = 120,
            message = "O título deve possuir no máximo 120 caracteres"
    )
    private String titulo;

    @NotNull(message = "A data de início é obrigatória")
    private LocalDate dataInicio;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }
}