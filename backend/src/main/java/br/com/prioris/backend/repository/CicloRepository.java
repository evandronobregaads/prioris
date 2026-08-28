package br.com.prioris.backend.repository;

import br.com.prioris.backend.entity.Ciclo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CicloRepository
        extends JpaRepository<Ciclo, Long> {

    List<Ciclo>
    findAllByUsuario_IdUsuarioAndStatusNotOrderByDataInicioDesc(
            Long idUsuario,
            String status
    );

    Optional<Ciclo>
    findByIdCicloAndUsuario_IdUsuarioAndStatusNot(
            Long idCiclo,
            Long idUsuario,
            String status
    );
}