package br.com.prioris.backend.repository;

import br.com.prioris.backend.entity.CicloObjetivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CicloObjetivoRepository
        extends JpaRepository<CicloObjetivo, Long> {

    boolean existsByCiclo_IdCicloAndObjetivo_IdObjetivo(
            Long idCiclo,
            Long idObjetivo
    );

    List<CicloObjetivo>
    findAllByCiclo_IdCicloOrderByIdCicloObjetivoAsc(
            Long idCiclo
    );

    Optional<CicloObjetivo>
    findByCiclo_IdCicloAndObjetivo_IdObjetivo(
            Long idCiclo,
            Long idObjetivo
    );
}