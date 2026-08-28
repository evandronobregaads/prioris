package br.com.prioris.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "prioridades_diarias",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_prioridades_diarias_usuario_data",
                        columnNames = {"id_usuario", "data_prioridade"}
                )
        }
)
public class PrioridadeDiaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prioridade_diaria")
    private Long idPrioridadeDiaria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tarefa", nullable = false)
    private Tarefa tarefa;

    @Column(name = "data_prioridade", nullable = false)
    private LocalDate dataPrioridade;

    public PrioridadeDiaria() {
    }

    public Long getIdPrioridadeDiaria() {
        return idPrioridadeDiaria;
    }

    public void setIdPrioridadeDiaria(Long idPrioridadeDiaria) {
        this.idPrioridadeDiaria = idPrioridadeDiaria;
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

    public LocalDate getDataPrioridade() {
        return dataPrioridade;
    }

    public void setDataPrioridade(LocalDate dataPrioridade) {
        this.dataPrioridade = dataPrioridade;
    }
}