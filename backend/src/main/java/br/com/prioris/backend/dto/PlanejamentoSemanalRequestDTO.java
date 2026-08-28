package br.com.prioris.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class PlanejamentoSemanalRequestDTO {

    private Long idCiclo;

    private Byte semanaCiclo;

    @NotNull(message = "A data de início da semana é obrigatória")
    private LocalDate dataInicioSemana;

    public Long getIdCiclo() {
        return idCiclo;
    }

    public void setIdCiclo(Long idCiclo) {
        this.idCiclo = idCiclo;
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
}