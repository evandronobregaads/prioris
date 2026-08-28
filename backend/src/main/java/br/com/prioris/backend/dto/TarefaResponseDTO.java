package br.com.prioris.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TarefaResponseDTO {

    private Long idTarefa;
    private Long idUsuario;
    private Long idMeta;
    private Long idObjetivo;
    private String titulo;
    private String descricao;
    private String classificacaoAbcde;
    private LocalDate dataPlanejada;
    private LocalDateTime prazo;
    private Integer tempoEstimado;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConclusao;

    public TarefaResponseDTO(
            Long idTarefa,
            Long idUsuario,
            Long idMeta,
            Long idObjetivo,
            String titulo,
            String descricao,
            String classificacaoAbcde,
            LocalDate dataPlanejada,
            LocalDateTime prazo,
            Integer tempoEstimado,
            String status,
            LocalDateTime dataCriacao,
            LocalDateTime dataConclusao
    ) {
        this.idTarefa = idTarefa;
        this.idUsuario = idUsuario;
        this.idMeta = idMeta;
        this.idObjetivo = idObjetivo;
        this.titulo = titulo;
        this.descricao = descricao;
        this.classificacaoAbcde = classificacaoAbcde;
        this.dataPlanejada = dataPlanejada;
        this.prazo = prazo;
        this.tempoEstimado = tempoEstimado;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataConclusao = dataConclusao;
    }

    public Long getIdTarefa() {
        return idTarefa;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getIdMeta() {
        return idMeta;
    }

    public Long getIdObjetivo() {
        return idObjetivo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getClassificacaoAbcde() {
        return classificacaoAbcde;
    }

    public LocalDate getDataPlanejada() {
        return dataPlanejada;
    }

    public LocalDateTime getPrazo() {
        return prazo;
    }

    public Integer getTempoEstimado() {
        return tempoEstimado;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }
}