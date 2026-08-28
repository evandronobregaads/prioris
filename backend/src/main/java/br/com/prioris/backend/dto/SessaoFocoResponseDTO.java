package br.com.prioris.backend.dto;

import java.time.LocalDateTime;

public class SessaoFocoResponseDTO {

    private Long idSessaoFoco;
    private Long idUsuario;
    private Long idTarefa;
    private String tituloTarefa;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Integer tempoFocoPlanejado;
    private Integer tempoDescansoPlanejado;
    private Integer tempoFocoRealizado;
    private String status;

    public SessaoFocoResponseDTO(
            Long idSessaoFoco,
            Long idUsuario,
            Long idTarefa,
            String tituloTarefa,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Integer tempoFocoPlanejado,
            Integer tempoDescansoPlanejado,
            Integer tempoFocoRealizado,
            String status
    ) {
        this.idSessaoFoco = idSessaoFoco;
        this.idUsuario = idUsuario;
        this.idTarefa = idTarefa;
        this.tituloTarefa = tituloTarefa;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.tempoFocoPlanejado = tempoFocoPlanejado;
        this.tempoDescansoPlanejado = tempoDescansoPlanejado;
        this.tempoFocoRealizado = tempoFocoRealizado;
        this.status = status;
    }

    public Long getIdSessaoFoco() {
        return idSessaoFoco;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getIdTarefa() {
        return idTarefa;
    }

    public String getTituloTarefa() {
        return tituloTarefa;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public Integer getTempoFocoPlanejado() {
        return tempoFocoPlanejado;
    }

    public Integer getTempoDescansoPlanejado() {
        return tempoDescansoPlanejado;
    }

    public Integer getTempoFocoRealizado() {
        return tempoFocoRealizado;
    }

    public String getStatus() {
        return status;
    }
}