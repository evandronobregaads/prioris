package br.com.prioris.backend.repository;

import br.com.prioris.backend.entity.RevisaoSemanal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RevisaoSemanalRepository
        extends JpaRepository<RevisaoSemanal, Long> {

    boolean existsByPlanejamentoSemanal_IdPlanejamentoSemanal(
            Long idPlanejamento
    );

    Optional<RevisaoSemanal>
    findByPlanejamentoSemanal_IdPlanejamentoSemanalAndPlanejamentoSemanal_Usuario_IdUsuario(
            Long idPlanejamento,
            Long idUsuario
    );

    List<RevisaoSemanal>
    findAllByPlanejamentoSemanal_Usuario_IdUsuarioOrderByPlanejamentoSemanal_DataInicioSemanaDesc(
            Long idUsuario
    );
}