package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.PrioridadeDiariaRequestDTO;
import br.com.prioris.backend.dto.PrioridadeDiariaResponseDTO;
import br.com.prioris.backend.service.PrioridadeDiariaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/usuarios/{idUsuario}/prioridade-diaria"
)
@Tag(
        name = "Prioridade Diária",
        description = "Definição e histórico da Prioridade #1 do dia"
)
public class PrioridadeDiariaController {

    private final PrioridadeDiariaService prioridadeService;

    public PrioridadeDiariaController(
            PrioridadeDiariaService prioridadeService
    ) {
        this.prioridadeService = prioridadeService;
    }

    @PostMapping
    public ResponseEntity<PrioridadeDiariaResponseDTO> definir(
            @PathVariable Long idUsuario,
            @Valid @RequestBody PrioridadeDiariaRequestDTO dto
    ) {

        PrioridadeDiariaResponseDTO prioridade =
                prioridadeService.definir(
                        idUsuario,
                        dto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(prioridade);
    }

    @GetMapping("/hoje")
    public ResponseEntity<PrioridadeDiariaResponseDTO> buscarHoje(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                prioridadeService.buscarHoje(idUsuario)
        );
    }

    @PatchMapping("/hoje")
    public ResponseEntity<PrioridadeDiariaResponseDTO> alterarHoje(
            @PathVariable Long idUsuario,
            @Valid @RequestBody PrioridadeDiariaRequestDTO dto
    ) {

        return ResponseEntity.ok(
                prioridadeService.alterarHoje(
                        idUsuario,
                        dto
                )
        );
    }

    @DeleteMapping("/hoje")
    public ResponseEntity<Void> excluirHoje(
            @PathVariable Long idUsuario
    ) {

        prioridadeService.excluirHoje(idUsuario);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/historico")
    public ResponseEntity<List<PrioridadeDiariaResponseDTO>>
    listarHistorico(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                prioridadeService.listarHistorico(idUsuario)
        );
    }
}