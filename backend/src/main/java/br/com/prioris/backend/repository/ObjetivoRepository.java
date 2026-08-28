package br.com.prioris.backend.repository;

import br.com.prioris.backend.entity.Objetivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObjetivoRepository
        extends JpaRepository<Objetivo, Long> {

    List<Objetivo> findAllByUsuario_IdUsuarioOrderByIdObjetivoAsc(
            Long idUsuario
    );

    Optional<Objetivo> findByIdObjetivoAndUsuario_IdUsuario(
            Long idObjetivo,
            Long idUsuario
    );

    List<Objetivo> findAllByUsuario_IdUsuarioAndStatusNotOrderByIdObjetivoAsc(
            Long idUsuario,
            String status
    );

    Optional<Objetivo> findByIdObjetivoAndUsuario_IdUsuarioAndStatusNot(
            Long idObjetivo,
            Long idUsuario,
            String status
    );
}