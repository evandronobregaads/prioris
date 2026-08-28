package br.com.prioris.backend.repository;

import br.com.prioris.backend.entity.Meta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MetaRepository extends JpaRepository<Meta, Long> {

    List<Meta>
    findAllByObjetivo_IdObjetivoAndObjetivo_Usuario_IdUsuarioAndStatusNotOrderByIdMetaAsc(
            Long idObjetivo,
            Long idUsuario,
            String status
    );

    Optional<Meta>
    findByIdMetaAndObjetivo_IdObjetivoAndObjetivo_Usuario_IdUsuarioAndStatusNot(
            Long idMeta,
            Long idObjetivo,
            Long idUsuario,
            String status
    );
}