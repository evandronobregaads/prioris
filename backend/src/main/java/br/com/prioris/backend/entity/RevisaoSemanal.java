package br.com.prioris.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "revisoes_semanais",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_revisoes_semanais_planejamento",
                        columnNames = "id_planejamento_semanal"
                )
        }
)
public class RevisaoSemanal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_revisao_semanal")
    private Long idRevisaoSemanal;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_planejamento_semanal",
            nullable = false,
            unique = true
    )
    private PlanejamentoSemanal planejamentoSemanal;

    @Column(
            name = "score_execucao",
            precision = 5,
            scale = 2
    )
    private BigDecimal scoreExecucao;

    @Column(
            name = "principais_conquistas",
            columnDefinition = "TEXT"
    )
    private String principaisConquistas;

    @Column(
            name = "dificuldades",
            columnDefinition = "TEXT"
    )
    private String dificuldades;

    @Column(
            name = "ajustes_proxima_semana",
            columnDefinition = "TEXT"
    )
    private String ajustesProximaSemana;

    @Column(
            name = "observacoes",
            columnDefinition = "TEXT"
    )
    private String observacoes;

    @CreationTimestamp
    @Column(
            name = "data_revisao",
            nullable = false,
            updatable = false
    )
    private LocalDateTime dataRevisao;

    public RevisaoSemanal() {
    }

    public Long getIdRevisaoSemanal() {
        return idRevisaoSemanal;
    }

    public void setIdRevisaoSemanal(Long idRevisaoSemanal) {
        this.idRevisaoSemanal = idRevisaoSemanal;
    }

    public PlanejamentoSemanal getPlanejamentoSemanal() {
        return planejamentoSemanal;
    }

    public void setPlanejamentoSemanal(
            PlanejamentoSemanal planejamentoSemanal
    ) {
        this.planejamentoSemanal = planejamentoSemanal;
    }

    public BigDecimal getScoreExecucao() {
        return scoreExecucao;
    }

    public void setScoreExecucao(BigDecimal scoreExecucao) {
        this.scoreExecucao = scoreExecucao;
    }

    public String getPrincipaisConquistas() {
        return principaisConquistas;
    }

    public void setPrincipaisConquistas(String principaisConquistas) {
        this.principaisConquistas = principaisConquistas;
    }

    public String getDificuldades() {
        return dificuldades;
    }

    public void setDificuldades(String dificuldades) {
        this.dificuldades = dificuldades;
    }

    public String getAjustesProximaSemana() {
        return ajustesProximaSemana;
    }

    public void setAjustesProximaSemana(String ajustesProximaSemana) {
        this.ajustesProximaSemana = ajustesProximaSemana;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getDataRevisao() {
        return dataRevisao;
    }
}