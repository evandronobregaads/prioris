package br.com.prioris.backend.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ObjetivoPatchDTO {

    @Size(min = 1, max = 150,
            message = "O título deve possuir entre 1 e 150 caracteres")
    private String titulo;

    private String descricao;

    @Size(min = 1, max = 50,
            message = "A área deve possuir entre 1 e 50 caracteres")
    private String area;

    private String motivo;

    private LocalDate prazo;

    @Pattern(
            regexp = "ATIVO|CONCLUIDO|PAUSADO",
            message = "O status deve ser ATIVO, CONCLUIDO ou PAUSADO"
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
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