package br.com.prioris.backend.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CicloPatchDTO {

    @Size(
            min = 1,
            max = 120,
            message = "O título deve possuir entre 1 e 120 caracteres"
    )
    private String titulo;

    private LocalDate dataInicio;

    @Pattern(
            regexp = "PLANEJADO|EM_ANDAMENTO|CONCLUIDO",
            message = "O status deve ser PLANEJADO, EM_ANDAMENTO ou CONCLUIDO"
    )
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
