package br.com.prioris.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MetaResponseDTO {

    private Long idMeta;
    private Long idObjetivo;
    private Long idUsuario;
    private String titulo;
    private String descricao;
    private LocalDate prazo;
    private String status;
    private LocalDateTime dataCriacao;

    public MetaResponseDTO(
            Long idMeta,
            Long idObjetivo,
            Long idUsuario,
            String titulo,
            String descricao,
            LocalDate prazo,
            String status,
            LocalDateTime dataCriacao
    ) {
        this.idMeta = idMeta;
        this.idObjetivo = idObjetivo;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.descricao = descricao;
        this.prazo = prazo;
        this.status = status;
        this.dataCriacao = dataCriacao;
    }

    public Long getIdMeta() {
        return idMeta;
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