package br.com.prioris.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "planejamentos_semanais")
public class PlanejamentoSemanal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planejamento_semanal")
    private Long idPlanejamentoSemanal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo")
    private Ciclo ciclo;

    @Column(name = "semana_ciclo")
    private Byte semanaCiclo;

    @Column(name = "data_inicio_semana", nullable = false)
    private LocalDate dataInicioSemana;

    @Column(name = "data_fim_semana", nullable = false)
    private LocalDate dataFimSemana;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    public PlanejamentoSemanal() {
    }

    public Long getIdPlanejamentoSemanal() {
        return idPlanejamentoSemanal;
    }

    public void setIdPlanejamentoSemanal(Long idPlanejamentoSemanal) {
        this.idPlanejamentoSemanal = idPlanejamentoSemanal;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

    public Byte getSemanaCiclo() {
        return semanaCiclo;
    }

    public void setSemanaCiclo(Byte semanaCiclo) {
        this.semanaCiclo = semanaCiclo;
    }

    public LocalDate getDataInicioSemana() {
        return dataInicioSemana;
    }

    public void setDataInicioSemana(LocalDate dataInicioSemana) {
        this.dataInicioSemana = dataInicioSemana;
    }

    public LocalDate getDataFimSemana() {
        return dataFimSemana;
    }

    public void setDataFimSemana(LocalDate dataFimSemana) {
        this.dataFimSemana = dataFimSemana;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}