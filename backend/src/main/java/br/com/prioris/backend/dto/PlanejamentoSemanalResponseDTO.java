package br.com.prioris.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PlanejamentoSemanalResponseDTO {

    private Long idPlanejamentoSemanal;
    private Long idUsuario;
    private Long idCiclo;
    private Byte semanaCiclo;
    private LocalDate dataInicioSemana;
    private LocalDate dataFimSemana;

    private Integer totalTarefasPlanejadas;
    private Integer totalTarefasConcluidas;
    private BigDecimal scoreExecucao;

    private LocalDateTime dataCriacao;

    public PlanejamentoSemanalResponseDTO(
            Long idPlanejamentoSemanal,
            Long idUsuario,
            Long idCiclo,
            Byte semanaCiclo,
            LocalDate dataInicioSemana,
            LocalDate dataFimSemana,
            Integer totalTarefasPlanejadas,
            Integer totalTarefasConcluidas,
            BigDecimal scoreExecucao,
            LocalDateTime dataCriacao
    ) {
        this.idPlanejamentoSemanal = idPlanejamentoSemanal;
        this.idUsuario = idUsuario;
        this.idCiclo = idCiclo;
        this.semanaCiclo = semanaCiclo;
        this.dataInicioSemana = dataInicioSemana;
        this.dataFimSemana = dataFimSemana;
        this.totalTarefasPlanejadas = totalTarefasPlanejadas;
        this.totalTarefasConcluidas = totalTarefasConcluidas;
        this.scoreExecucao = scoreExecucao;
        this.dataCriacao = dataCriacao;
    }

    public Long getIdPlanejamentoSemanal() {
        return idPlanejamentoSemanal;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getIdCiclo() {
        return idCiclo;
    }

    public Byte getSemanaCiclo() {
        return semanaCiclo;
    }

    public LocalDate getDataInicioSemana() {
        return dataInicioSemana;
    }

    public LocalDate getDataFimSemana() {
        return dataFimSemana;
    }

    public Integer getTotalTarefasPlanejadas() {
        return totalTarefasPlanejadas;
    }

    public Integer getTotalTarefasConcluidas() {
        return totalTarefasConcluidas;
    }

    public BigDecimal getScoreExecucao() {
        return scoreExecucao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}