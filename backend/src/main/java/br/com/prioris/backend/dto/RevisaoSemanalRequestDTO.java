package br.com.prioris.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class RevisaoSemanalRequestDTO {

    @NotBlank(message = "Informe as principais conquistas da semana")
    private String principaisConquistas;

    @NotBlank(message = "Informe as dificuldades da semana")
    private String dificuldades;

    @NotBlank(message = "Informe os ajustes para a próxima semana")
    private String ajustesProximaSemana;

    private String observacoes;

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
}