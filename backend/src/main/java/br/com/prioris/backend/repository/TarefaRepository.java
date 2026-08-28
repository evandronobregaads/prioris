package br.com.prioris.backend.repository;

import br.com.prioris.backend.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa>
    findAllByUsuario_IdUsuarioAndStatusNotOrderByIdTarefaAsc(
            Long idUsuario,
            String status
    );

    Optional<Tarefa>
    findByIdTarefaAndUsuario_IdUsuarioAndStatusNot(
            Long idTarefa,
            Long idUsuario,
            String status
    );
}