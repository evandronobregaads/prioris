package br.com.prioris.backend.repository;

import br.com.prioris.backend.entity.SessaoFoco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessaoFocoRepository
        extends JpaRepository<SessaoFoco, Long> {

    List<SessaoFoco>
    findAllByUsuario_IdUsuarioOrderByDataInicioDesc(
            Long idUsuario
    );

    Optional<SessaoFoco>
    findByIdSessaoFocoAndUsuario_IdUsuario(
            Long idSessaoFoco,
            Long idUsuario
    );
}