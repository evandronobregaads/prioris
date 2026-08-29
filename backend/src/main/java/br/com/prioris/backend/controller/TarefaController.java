package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.service.TarefaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}/tarefas")
@Tag(
        name = "Tarefas",
        description = "Gerenciamento de tarefas e classificação ABCDE"
)
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @GetMapping
    public ResponseEntity<List<TarefaResponseDTO>> listarTodos(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                tarefaService.listarTodos(idUsuario)
        );
    }

    @GetMapping("/{idTarefa}")
    public ResponseEntity<TarefaResponseDTO> buscarPorId(
            @PathVariable Long idUsuario,
            @PathVariable Long idTarefa
    ) {

        return ResponseEntity.ok(
                tarefaService.buscarPorId(
                        idUsuario,
                        idTarefa
                )
        );
    }

    @PostMapping
    public ResponseEntity<TarefaResponseDTO> cadastrar(
            @PathVariable Long idUsuario,
            @Valid @RequestBody TarefaRequestDTO dto
    ) {

        TarefaResponseDTO tarefa =
                tarefaService.cadastrar(
                        idUsuario,
                        dto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tarefa);
    }

    @PutMapping("/{idTarefa}")
    public ResponseEntity<TarefaResponseDTO> atualizar(
            @PathVariable Long idUsuario,
            @PathVariable Long idTarefa,
            @Valid @RequestBody TarefaAtualizacaoDTO dto
    ) {

        return ResponseEntity.ok(
                tarefaService.atualizar(
                        idUsuario,
                        idTarefa,
                        dto
                )
        );
    }

    @PatchMapping("/{idTarefa}")
    public ResponseEntity<TarefaResponseDTO> atualizarParcialmente(
            @PathVariable Long idUsuario,
            @PathVariable Long idTarefa,
            @Valid @RequestBody TarefaPatchDTO dto
    ) {

        return ResponseEntity.ok(
                tarefaService.atualizarParcialmente(
                        idUsuario,
                        idTarefa,
                        dto
                )
        );
    }

    @DeleteMapping("/{idTarefa}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long idUsuario,
            @PathVariable Long idTarefa
    ) {

        tarefaService.excluir(
                idUsuario,
                idTarefa
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}