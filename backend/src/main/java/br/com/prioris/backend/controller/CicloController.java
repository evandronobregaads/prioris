package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.service.CicloService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}/ciclos")
@Tag(
        name = "Ciclos de 12 Semanas",
        description = "Gerenciamento dos ciclos e seus objetivos"
)
public class CicloController {

    private final CicloService cicloService;

    public CicloController(CicloService cicloService) {
        this.cicloService = cicloService;
    }

    @GetMapping
    public ResponseEntity<List<CicloResponseDTO>> listarTodos(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                cicloService.listarTodos(idUsuario)
        );
    }

    @GetMapping("/{idCiclo}")
    public ResponseEntity<CicloResponseDTO> buscarPorId(
            @PathVariable Long idUsuario,
            @PathVariable Long idCiclo
    ) {

        return ResponseEntity.ok(
                cicloService.buscarPorId(
                        idUsuario,
                        idCiclo
                )
        );
    }

    @PostMapping
    public ResponseEntity<CicloResponseDTO> cadastrar(
            @PathVariable Long idUsuario,
            @Valid @RequestBody CicloRequestDTO dto
    ) {

        CicloResponseDTO ciclo =
                cicloService.cadastrar(
                        idUsuario,
                        dto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ciclo);
    }

    @PutMapping("/{idCiclo}")
    public ResponseEntity<CicloResponseDTO> atualizar(
            @PathVariable Long idUsuario,
            @PathVariable Long idCiclo,
            @Valid @RequestBody CicloAtualizacaoDTO dto
    ) {

        return ResponseEntity.ok(
                cicloService.atualizar(
                        idUsuario,
                        idCiclo,
                        dto
                )
        );
    }

    @PatchMapping("/{idCiclo}")
    public ResponseEntity<CicloResponseDTO>
    atualizarParcialmente(
            @PathVariable Long idUsuario,
            @PathVariable Long idCiclo,
            @Valid @RequestBody CicloPatchDTO dto
    ) {

        return ResponseEntity.ok(
                cicloService.atualizarParcialmente(
                        idUsuario,
                        idCiclo,
                        dto
                )
        );
    }

    @DeleteMapping("/{idCiclo}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long idUsuario,
            @PathVariable Long idCiclo
    ) {

        cicloService.excluir(
                idUsuario,
                idCiclo
        );

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping(
            "/{idCiclo}/objetivos/{idObjetivo}"
    )
    public ResponseEntity<CicloObjetivoResponseDTO>
    associarObjetivo(
            @PathVariable Long idUsuario,
            @PathVariable Long idCiclo,
            @PathVariable Long idObjetivo
    ) {

        CicloObjetivoResponseDTO associacao =
                cicloService.associarObjetivo(
                        idUsuario,
                        idCiclo,
                        idObjetivo
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(associacao);
    }

    @GetMapping("/{idCiclo}/objetivos")
    public ResponseEntity<List<CicloObjetivoResponseDTO>>
    listarObjetivos(
            @PathVariable Long idUsuario,
            @PathVariable Long idCiclo
    ) {

        return ResponseEntity.ok(
                cicloService.listarObjetivos(
                        idUsuario,
                        idCiclo
                )
        );
    }

    @DeleteMapping(
            "/{idCiclo}/objetivos/{idObjetivo}"
    )
    public ResponseEntity<Void> removerObjetivo(
            @PathVariable Long idUsuario,
            @PathVariable Long idCiclo,
            @PathVariable Long idObjetivo
    ) {

        cicloService.removerObjetivo(
                idUsuario,
                idCiclo,
                idObjetivo
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}