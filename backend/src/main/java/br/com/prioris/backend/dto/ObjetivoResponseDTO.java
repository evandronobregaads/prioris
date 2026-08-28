package br.com.prioris.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ObjetivoResponseDTO {

    private Long idObjetivo;
    private Long idUsuario;
    private String titulo;
    private String descricao;
    private String area;
    private String motivo;
    private LocalDate prazo;
    private String status;
    private LocalDateTime dataCriacao;

    public ObjetivoResponseDTO(
            Long idObjetivo,
            Long idUsuario,
            String titulo,
            String descricao,
            String area,
            String motivo,
            LocalDate prazo,
            String status,
            LocalDateTime dataCriacao
    ) {
        this.idObjetivo = idObjetivo;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.descricao = descricao;
        this.area = area;
        this.motivo = motivo;
        this.prazo = prazo;
        this.status = status;
        this.dataCriacao = dataCriacao;
    }

    public Long getIdObjetivo() {
        return idObjetivo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getArea() {
        return area;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}