package br.com.prioris.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessoes_foco")
public class SessaoFoco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sessao_foco")
    private Long idSessaoFoco;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tarefa")
    private Tarefa tarefa;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Column(name = "tempo_foco_planejado", nullable = false)
    private Integer tempoFocoPlanejado;

    @Column(name = "tempo_descanso_planejado", nullable = false)
    private Integer tempoDescansoPlanejado;

    @Column(name = "tempo_foco_realizado", nullable = false)
    private Integer tempoFocoRealizado = 0;

    @Column(nullable = false, length = 20)
    private String status = "EM_ANDAMENTO";

    public SessaoFoco() {
    }

    public Long getIdSessaoFoco() {
        return idSessaoFoco;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public Integer getTempoFocoPlanejado() {
        return tempoFocoPlanejado;
    }

    public void setTempoFocoPlanejado(Integer tempoFocoPlanejado) {
        this.tempoFocoPlanejado = tempoFocoPlanejado;
    }

    public Integer getTempoDescansoPlanejado() {
        return tempoDescansoPlanejado;
    }

    public void setTempoDescansoPlanejado(Integer tempoDescansoPlanejado) {
        this.tempoDescansoPlanejado = tempoDescansoPlanejado;
    }

    public Integer getTempoFocoRealizado() {
        return tempoFocoRealizado;
    }

    public void setTempoFocoRealizado(Integer tempoFocoRealizado) {
        this.tempoFocoRealizado = tempoFocoRealizado;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}