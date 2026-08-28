package br.com.prioris.backend.repository;

import br.com.prioris.backend.entity.PlanejamentoTarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanejamentoTarefaRepository
        extends JpaRepository<PlanejamentoTarefa, Long> {

    boolean existsByPlanejamentoSemanal_IdPlanejamentoSemanalAndTarefa_IdTarefa(
            Long idPlanejamentoSemanal,
            Long idTarefa
    );

    List<PlanejamentoTarefa>
    findAllByPlanejamentoSemanal_IdPlanejamentoSemanalOrderByIdPlanejamentoTarefaAsc(
            Long idPlanejamentoSemanal
    );

    Optional<PlanejamentoTarefa>
    findByPlanejamentoSemanal_IdPlanejamentoSemanalAndTarefa_IdTarefa(
            Long idPlanejamentoSemanal,
            Long idTarefa
    );
}