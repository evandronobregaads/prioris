package br.com.prioris.backend.repository;

import br.com.prioris.backend.entity.PrioridadeDiaria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PrioridadeDiariaRepository
        extends JpaRepository<PrioridadeDiaria, Long> {

    Optional<PrioridadeDiaria>
    findByUsuario_IdUsuarioAndDataPrioridade(
            Long idUsuario,
            LocalDate dataPrioridade
    );

    boolean existsByUsuario_IdUsuarioAndDataPrioridade(
            Long idUsuario,
            LocalDate dataPrioridade
    );

    List<PrioridadeDiaria>
    findAllByUsuario_IdUsuarioOrderByDataPrioridadeDesc(
            Long idUsuario
    );
}