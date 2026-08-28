package br.com.prioris.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CicloResponseDTO {

    private Long idCiclo;
    private Long idUsuario;
    private String titulo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String status;
    private LocalDateTime dataCriacao;

    public CicloResponseDTO(
            Long idCiclo,
            Long idUsuario,
            String titulo,
            LocalDate dataInicio,
            LocalDate dataFim,
            String status,
            LocalDateTime dataCriacao
    ) {
        this.idCiclo = idCiclo;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
        this.dataCriacao = dataCriacao;
    }

    public Long getIdCiclo() {
        return idCiclo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}