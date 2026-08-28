package br.com.prioris.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SessaoFocoRequestDTO {

    private Long idTarefa;

    @NotNull(message = "O tempo de foco é obrigatório")
    @Positive(message = "O tempo de foco deve ser maior que zero")
    private Integer tempoFocoPlanejado;

    @NotNull(message = "O tempo de descanso é obrigatório")
    @Positive(message = "O tempo de descanso deve ser maior que zero")
    private Integer tempoDescansoPlanejado;

    public Long getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(Long idTarefa) {
        this.idTarefa = idTarefa;
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
}