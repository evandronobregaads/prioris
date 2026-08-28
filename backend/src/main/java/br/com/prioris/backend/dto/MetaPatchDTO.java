package br.com.prioris.backend.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class MetaPatchDTO {

    @Size(
            min = 1,
            max = 150,
            message = "O título deve possuir entre 1 e 150 caracteres"
    )
    private String titulo;

    private String descricao;

    private LocalDate prazo;

    @Pattern(
            regexp = "PENDENTE|EM_ANDAMENTO|CONCLUIDA",
            message = "O status deve ser PENDENTE, EM_ANDAMENTO ou CONCLUIDA"
    )
    private String status;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public void setPrazo(LocalDate prazo) {
        this.prazo = prazo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}