package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.service.PlanejamentoSemanalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/usuarios/{idUsuario}/planejamentos-semanais"
)
@Tag(
        name = "Planejamento Semanal",
        description = "Planejamento das tarefas estratégicas e Score de Execução"
)
public class PlanejamentoSemanalController {

    private final PlanejamentoSemanalService planejamentoService;

    public PlanejamentoSemanalController(
            PlanejamentoSemanalService planejamentoService
    ) {
        this.planejamentoService = planejamentoService;
    }

    @GetMapping
    public ResponseEntity<List<PlanejamentoSemanalResponseDTO>>
    listarTodos(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                planejamentoService.listarTodos(
                        idUsuario
                )
        );
    }

    @GetMapping("/{idPlanejamento}")
    public ResponseEntity<PlanejamentoSemanalResponseDTO>
    buscarPorId(
            @PathVariable Long idUsuario,
            @PathVariable Long idPlanejamento
    ) {

        return ResponseEntity.ok(
                planejamentoService.buscarPorId(
                        idUsuario,
                        idPlanejamento
                )
        );
    }

    @PostMapping
    public ResponseEntity<PlanejamentoSemanalResponseDTO>
    cadastrar(
            @PathVariable Long idUsuario,
            @Valid @RequestBody
            PlanejamentoSemanalRequestDTO dto
    ) {

        PlanejamentoSemanalResponseDTO planejamento =
                planejamentoService.cadastrar(
                        idUsuario,
                        dto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(planejamento);
    }

    @PostMapping(
            "/{idPlanejamento}/tarefas/{idTarefa}"
    )
    public ResponseEntity<PlanejamentoTarefaResponseDTO>
    adicionarTarefa(
            @PathVariable Long idUsuario,
            @PathVariable Long idPlanejamento,
            @PathVariable Long idTarefa
    ) {

        PlanejamentoTarefaResponseDTO tarefa =
                planejamentoService.adicionarTarefa(
                        idUsuario,
                        idPlanejamento,
                        idTarefa
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tarefa);
    }

    @GetMapping(
            "/{idPlanejamento}/tarefas"
    )
    public ResponseEntity<List<PlanejamentoTarefaResponseDTO>>
    listarTarefas(
            @PathVariable Long idUsuario,
            @PathVariable Long idPlanejamento
    ) {

        return ResponseEntity.ok(
                planejamentoService.listarTarefas(
                        idUsuario,
                        idPlanejamento
                )
        );
    }

    @DeleteMapping(
            "/{idPlanejamento}/tarefas/{idTarefa}"
    )
    public ResponseEntity<Void> removerTarefa(
            @PathVariable Long idUsuario,
            @PathVariable Long idPlanejamento,
            @PathVariable Long idTarefa
    ) {

        planejamentoService.removerTarefa(
                idUsuario,
                idPlanejamento,
                idTarefa
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}