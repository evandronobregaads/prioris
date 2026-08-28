package br.com.prioris.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class SessaoFocoFinalizacaoDTO {

    @NotNull(message = "Informe o tempo de foco realizado")
    @PositiveOrZero(
            message = "O tempo realizado não pode ser negativo"
    )
    private Integer tempoFocoRealizado;

    public Integer getTempoFocoRealizado() {
        return tempoFocoRealizado;
    }

    public void setTempoFocoRealizado(Integer tempoFocoRealizado) {
        this.tempoFocoRealizado = tempoFocoRealizado;
    }
}