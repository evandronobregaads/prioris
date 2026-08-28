package br.com.prioris.backend.repository;

import br.com.prioris.backend.entity.PlanejamentoSemanal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanejamentoSemanalRepository
        extends JpaRepository<PlanejamentoSemanal, Long> {

    List<PlanejamentoSemanal>
    findAllByUsuario_IdUsuarioOrderByDataInicioSemanaDesc(
            Long idUsuario
    );

    Optional<PlanejamentoSemanal>
    findByIdPlanejamentoSemanalAndUsuario_IdUsuario(
            Long idPlanejamentoSemanal,
            Long idUsuario
    );
}