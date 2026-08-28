package br.com.prioris.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "planejamentos_tarefas",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_planejamentos_tarefas",
                        columnNames = {
                                "id_planejamento_semanal",
                                "id_tarefa"
                        }
                )
        }
)
public class PlanejamentoTarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planejamento_tarefa")
    private Long idPlanejamentoTarefa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_planejamento_semanal",
            nullable = false
    )
    private PlanejamentoSemanal planejamentoSemanal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tarefa", nullable = false)
    private Tarefa tarefa;

    public PlanejamentoTarefa() {
    }

    public Long getIdPlanejamentoTarefa() {
        return idPlanejamentoTarefa;
    }

    public void setIdPlanejamentoTarefa(Long idPlanejamentoTarefa) {
        this.idPlanejamentoTarefa = idPlanejamentoTarefa;
    }

    public PlanejamentoSemanal getPlanejamentoSemanal() {
        return planejamentoSemanal;
    }

    public void setPlanejamentoSemanal(
            PlanejamentoSemanal planejamentoSemanal
    ) {
        this.planejamentoSemanal = planejamentoSemanal;
    }

    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }
}