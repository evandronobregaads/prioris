package br.com.prioris.backend.dto;

public class RevisaoSemanalPatchDTO {

    private String principaisConquistas;
    private String dificuldades;
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