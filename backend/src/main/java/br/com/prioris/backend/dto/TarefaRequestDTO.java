package br.com.prioris.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TarefaRequestDTO {

    private Long idMeta;
    private Long idObjetivo;

    @NotBlank(message = "O título é obrigatório")
    @Size(
            max = 180,
            message = "O título deve possuir no máximo 180 caracteres"
    )
    private String titulo;

    private String descricao;

    @Pattern(
            regexp = "A|B|C|D|E",
            message = "A classificação ABCDE deve ser A, B, C, D ou E"
    )
    private String classificacaoAbcde;

    private LocalDate dataPlanejada;

    private LocalDateTime prazo;

    @Positive(message = "O tempo estimado deve ser maior que zero")
    private Integer tempoEstimado;

    public Long getIdMeta() {
        return idMeta;
    }

    public void setIdMeta(Long idMeta) {
        this.idMeta = idMeta;
    }

    public Long getIdObjetivo() {
        return idObjetivo;
    }

    public void setIdObjetivo(Long idObjetivo) {
        this.idObjetivo = idObjetivo;
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
}