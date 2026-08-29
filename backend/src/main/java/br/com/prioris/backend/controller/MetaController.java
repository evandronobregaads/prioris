package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.service.MetaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/usuarios/{idUsuario}/objetivos/{idObjetivo}/metas"
)
@Tag(
        name = "Metas",
        description = "Gerenciamento das metas vinculadas aos objetivos"
)
public class MetaController {

    private final MetaService metaService;

    public MetaController(MetaService metaService) {
        this.metaService = metaService;
    }

    @GetMapping
    public ResponseEntity<List<MetaResponseDTO>> listarTodos(
            @PathVariable Long idUsuario,
            @PathVariable Long idObjetivo
    ) {

        return ResponseEntity.ok(
                metaService.listarTodos(
                        idUsuario,
                        idObjetivo
                )
        );
    }

    @GetMapping("/{idMeta}")
    public ResponseEntity<MetaResponseDTO> buscarPorId(
            @PathVariable Long idUsuario,
            @PathVariable Long idObjetivo,
            @PathVariable Long idMeta
    ) {

        return ResponseEntity.ok(
                metaService.buscarPorId(
                        idUsuario,
                        idObjetivo,
                        idMeta
                )
        );
    }

    @PostMapping
    public ResponseEntity<MetaResponseDTO> cadastrar(
            @PathVariable Long idUsuario,
            @PathVariable Long idObjetivo,
            @Valid @RequestBody MetaRequestDTO dto
    ) {

        MetaResponseDTO meta =
                metaService.cadastrar(
                        idUsuario,
                        idObjetivo,
                        dto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meta);
    }

    @PutMapping("/{idMeta}")
    public ResponseEntity<MetaResponseDTO> atualizar(
            @PathVariable Long idUsuario,
            @PathVariable Long idObjetivo,
            @PathVariable Long idMeta,
            @Valid @RequestBody MetaAtualizacaoDTO dto
    ) {

        return ResponseEntity.ok(
                metaService.atualizar(
                        idUsuario,
                        idObjetivo,
                        idMeta,
                        dto
                )
        );
    }

    @PatchMapping("/{idMeta}")
    public ResponseEntity<MetaResponseDTO> atualizarParcialmente(
            @PathVariable Long idUsuario,
            @PathVariable Long idObjetivo,
            @PathVariable Long idMeta,
            @Valid @RequestBody MetaPatchDTO dto
    ) {

        return ResponseEntity.ok(
                metaService.atualizarParcialmente(
                        idUsuario,
                        idObjetivo,
                        idMeta,
                        dto
                )
        );
    }

    @DeleteMapping("/{idMeta}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long idUsuario,
            @PathVariable Long idObjetivo,
            @PathVariable Long idMeta
    ) {

        metaService.excluir(
                idUsuario,
                idObjetivo,
                idMeta
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}