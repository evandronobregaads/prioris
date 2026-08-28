package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.ObjetivoAtualizacaoDTO;
import br.com.prioris.backend.dto.ObjetivoPatchDTO;
import br.com.prioris.backend.dto.ObjetivoRequestDTO;
import br.com.prioris.backend.dto.ObjetivoResponseDTO;
import br.com.prioris.backend.service.ObjetivoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}/objetivos")
public class ObjetivoController {

    private final ObjetivoService objetivoService;

    public ObjetivoController(ObjetivoService objetivoService) {
        this.objetivoService = objetivoService;
    }

    @GetMapping
    public ResponseEntity<List<ObjetivoResponseDTO>> listarTodos(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                objetivoService.listarTodos(idUsuario)
        );
    }

    @GetMapping("/{idObjetivo}")
    public ResponseEntity<ObjetivoResponseDTO> buscarPorId(
            @PathVariable Long idUsuario,
            @PathVariable Long idObjetivo
    ) {

        return ResponseEntity.ok(
                objetivoService.buscarPorId(
                        idUsuario,
                        idObjetivo
                )
        );
    }

    @PostMapping
    public ResponseEntity<ObjetivoResponseDTO> cadastrar(
            @PathVariable Long idUsuario,
            @Valid @RequestBody ObjetivoRequestDTO dto
    ) {

        ObjetivoResponseDTO objetivo =
                objetivoService.cadastrar(
                        idUsuario,
                        dto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(objetivo);
    }

    @PutMapping("/{idObjetivo}")
    public ResponseEntity<ObjetivoResponseDTO> atualizar(
            @PathVariable Long idUsuario,
            @PathVariable Long idObjetivo,
            @Valid @RequestBody ObjetivoAtualizacaoDTO dto
    ) {

        return ResponseEntity.ok(
                objetivoService.atualizar(
                        idUsuario,
                        idObjetivo,
                        dto
                )
        );
    }

    @PatchMapping("/{idObjetivo}")
    public ResponseEntity<ObjetivoResponseDTO> atualizarParcialmente(
            @PathVariable Long idUsuario,
            @PathVariable Long idObjetivo,
            @Valid @RequestBody ObjetivoPatchDTO dto
    ) {

        return ResponseEntity.ok(
                objetivoService.atualizarParcialmente(
                        idUsuario,
                        idObjetivo,
                        dto
                )
        );
    }

    @DeleteMapping("/{idObjetivo}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long idUsuario,
            @PathVariable Long idObjetivo
    ) {

        objetivoService.excluir(
                idUsuario,
                idObjetivo
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}