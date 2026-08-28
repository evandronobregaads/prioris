package br.com.prioris.backend.dto;

public class CicloObjetivoResponseDTO {

    private Long idCicloObjetivo;
    private Long idCiclo;
    private Long idObjetivo;
    private String tituloObjetivo;
    private String area;
    private String statusObjetivo;

    public CicloObjetivoResponseDTO(
            Long idCicloObjetivo,
            Long idCiclo,
            Long idObjetivo,
            String tituloObjetivo,
            String area,
            String statusObjetivo
    ) {
        this.idCicloObjetivo = idCicloObjetivo;
        this.idCiclo = idCiclo;
        this.idObjetivo = idObjetivo;
        this.tituloObjetivo = tituloObjetivo;
        this.area = area;
        this.statusObjetivo = statusObjetivo;
    }

    public Long getIdCicloObjetivo() {
        return idCicloObjetivo;
    }

    public Long getIdCiclo() {
        return idCiclo;
    }

    public Long getIdObjetivo() {
        return idObjetivo;
    }

    public String getTituloObjetivo() {
        return tituloObjetivo;
    }

    public String getArea() {
        return area;
    }

    public String getStatusObjetivo() {
        return statusObjetivo;
    }
}