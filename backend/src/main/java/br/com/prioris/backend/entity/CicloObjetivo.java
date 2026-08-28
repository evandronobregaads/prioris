package br.com.prioris.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "ciclos_objetivos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_ciclos_objetivos",
                        columnNames = {
                                "id_ciclo",
                                "id_objetivo"
                        }
                )
        }
)
public class CicloObjetivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ciclo_objetivo")
    private Long idCicloObjetivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ciclo", nullable = false)
    private Ciclo ciclo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_objetivo", nullable = false)
    private Objetivo objetivo;

    public CicloObjetivo() {
    }

    public Long getIdCicloObjetivo() {
        return idCicloObjetivo;
    }

    public void setIdCicloObjetivo(Long idCicloObjetivo) {
        this.idCicloObjetivo = idCicloObjetivo;
    }

    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

    public Objetivo getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(Objetivo objetivo) {
        this.objetivo = objetivo;
    }
}