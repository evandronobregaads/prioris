package br.com.prioris.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarefas")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarefa")
    private Long idTarefa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_meta")
    private Meta meta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_objetivo")
    private Objetivo objetivo;

    @Column(nullable = false, length = 180)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "classificacao_abcde", length = 1)
    private String classificacaoAbcde;

    @Column(name = "data_planejada")
    private LocalDate dataPlanejada;

    private LocalDateTime prazo;

    @Column(name = "tempo_estimado")
    private Integer tempoEstimado;

    @Column(nullable = false, length = 20)
    private String status = "PENDENTE";

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(
            name = "data_conclusao",
            insertable = false,
            updatable = false
    )
    private LocalDateTime dataConclusao;

    public Tarefa() {
    }

    public Long getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(Long idTarefa) {
        this.idTarefa = idTarefa;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Objetivo getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(Objetivo objetivo) {
        this.objetivo = objetivo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getClassificacaoAbcde() {
        return classificacaoAbcde;
    }

    public void setClassificacaoAbcde(String classificacaoAbcde) {
        this.classificacaoAbcde = classificacaoAbcde;
    }

    public LocalDate getDataPlanejada() {
        return dataPlanejada;
    }

    public void setDataPlanejada(LocalDate dataPlanejada) {
        this.dataPlanejada = dataPlanejada;
    }

    public LocalDateTime getPrazo() {
        return prazo;
    }

    public void setPrazo(LocalDateTime prazo) {
        this.prazo = prazo;
    }

    public Integer getTempoEstimado() {
        return tempoEstimado;
    }

    public void setTempoEstimado(Integer tempoEstimado) {
        this.tempoEstimado = tempoEstimado;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }
}