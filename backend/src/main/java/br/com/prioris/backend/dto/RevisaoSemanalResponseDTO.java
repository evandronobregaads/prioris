package br.com.prioris.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RevisaoSemanalResponseDTO {

    private Long idRevisaoSemanal;
    private Long idPlanejamentoSemanal;
    private Byte semanaCiclo;
    private LocalDate dataInicioSemana;
    private LocalDate dataFimSemana;
    private BigDecimal scoreExecucao;
    private String principaisConquistas;
    private String dificuldades;
    private String ajustesProximaSemana;
    private String observacoes;
    private LocalDateTime dataRevisao;

    public RevisaoSemanalResponseDTO(
            Long idRevisaoSemanal,
            Long idPlanejamentoSemanal,
            Byte semanaCiclo,
            LocalDate dataInicioSemana,
            LocalDate dataFimSemana,
            BigDecimal scoreExecucao,
            String principaisConquistas,
            String dificuldades,
            String ajustesProximaSemana,
            String observacoes,
            LocalDateTime dataRevisao
    ) {
        this.idRevisaoSemanal = idRevisaoSemanal;
        this.idPlanejamentoSemanal = idPlanejamentoSemanal;
        this.semanaCiclo = semanaCiclo;
        this.dataInicioSemana = dataInicioSemana;
        this.dataFimSemana = dataFimSemana;
        this.scoreExecucao = scoreExecucao;
        this.principaisConquistas = principaisConquistas;
        this.dificuldades = dificuldades;
        this.ajustesProximaSemana = ajustesProximaSemana;
        this.observacoes = observacoes;
        this.dataRevisao = dataRevisao;
    }

    public Long getIdRevisaoSemanal() {
        return idRevisaoSemanal;
    }

    public Long getIdPlanejamentoSemanal() {
        return idPlanejamentoSemanal;
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

    public BigDecimal getScoreExecucao() {
        return scoreExecucao;
    }

    public String getPrincipaisConquistas() {
        return principaisConquistas;
    }

    public String getDificuldades() {
        return dificuldades;
    }

    public String getAjustesProximaSemana() {
        return ajustesProximaSemana;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public LocalDateTime getDataRevisao() {
        return dataRevisao;
    }
}