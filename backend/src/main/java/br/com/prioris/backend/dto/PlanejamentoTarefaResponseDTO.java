package br.com.prioris.backend.dto;

public class PlanejamentoTarefaResponseDTO {

    private Long idPlanejamentoTarefa;
    private Long idTarefa;
    private String titulo;
    private String classificacaoAbcde;
    private String status;
    private Integer tempoEstimado;

    public PlanejamentoTarefaResponseDTO(
            Long idPlanejamentoTarefa,
            Long idTarefa,
            String titulo,
            String classificacaoAbcde,
            String status,
            Integer tempoEstimado
    ) {
        this.idPlanejamentoTarefa = idPlanejamentoTarefa;
        this.idTarefa = idTarefa;
        this.titulo = titulo;
        this.classificacaoAbcde = classificacaoAbcde;
        this.status = status;
        this.tempoEstimado = tempoEstimado;
    }

    public Long getIdPlanejamentoTarefa() {
        return idPlanejamentoTarefa;
    }

    public Long getIdTarefa() {
        return idTarefa;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getClassificacaoAbcde() {
        return classificacaoAbcde;
    }

    public String getStatus() {
        return status;
    }

    public Integer getTempoEstimado() {
        return tempoEstimado;
    }
}