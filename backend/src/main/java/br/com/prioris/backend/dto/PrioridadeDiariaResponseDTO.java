package br.com.prioris.backend.dto;

import java.time.LocalDate;

public class PrioridadeDiariaResponseDTO {

    private Long idPrioridadeDiaria;
    private Long idUsuario;
    private Long idTarefa;
    private String tituloTarefa;
    private String classificacaoAbcde;
    private String statusTarefa;
    private LocalDate dataPrioridade;

    public PrioridadeDiariaResponseDTO(
            Long idPrioridadeDiaria,
            Long idUsuario,
            Long idTarefa,
            String tituloTarefa,
            String classificacaoAbcde,
            String statusTarefa,
            LocalDate dataPrioridade
    ) {
        this.idPrioridadeDiaria = idPrioridadeDiaria;
        this.idUsuario = idUsuario;
        this.idTarefa = idTarefa;
        this.tituloTarefa = tituloTarefa;
        this.classificacaoAbcde = classificacaoAbcde;
        this.statusTarefa = statusTarefa;
        this.dataPrioridade = dataPrioridade;
    }

    public Long getIdPrioridadeDiaria() {
        return idPrioridadeDiaria;
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

    public String getClassificacaoAbcde() {
        return classificacaoAbcde;
    }

    public String getStatusTarefa() {
        return statusTarefa;
    }

    public LocalDate getDataPrioridade() {
        return dataPrioridade;
    }
}