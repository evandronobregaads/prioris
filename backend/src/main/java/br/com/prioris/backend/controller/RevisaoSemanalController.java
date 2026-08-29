package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.service.RevisaoSemanalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}")
@Tag(
        name = "Revisão Semanal",
        description = "Registro das revisões e evolução semanal"
)
public class RevisaoSemanalController {

    private final RevisaoSemanalService revisaoService;

    public RevisaoSemanalController(
            RevisaoSemanalService revisaoService
    ) {
        this.revisaoService = revisaoService;
    }

    @PostMapping(
            "/planejamentos-semanais/{idPlanejamento}/revisao"
    )
    public ResponseEntity<RevisaoSemanalResponseDTO> cadastrar(
            @PathVariable Long idUsuario,
            @PathVariable Long idPlanejamento,
            @Valid @RequestBody RevisaoSemanalRequestDTO dto
    ) {

        RevisaoSemanalResponseDTO revisao =
                revisaoService.cadastrar(
                        idUsuario,
                        idPlanejamento,
                        dto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(revisao);
    }

    @GetMapping(
            "/planejamentos-semanais/{idPlanejamento}/revisao"
    )
    public ResponseEntity<RevisaoSemanalResponseDTO> buscar(
            @PathVariable Long idUsuario,
            @PathVariable Long idPlanejamento
    ) {

        return ResponseEntity.ok(
                revisaoService.buscar(
                        idUsuario,
                        idPlanejamento
                )
        );
    }

    @GetMapping("/revisoes-semanais")
    public ResponseEntity<List<RevisaoSemanalResponseDTO>>
    listarHistorico(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                revisaoService.listarHistorico(
                        idUsuario
                )
        );
    }

    @PutMapping(
            "/planejamentos-semanais/{idPlanejamento}/revisao"
    )
    public ResponseEntity<RevisaoSemanalResponseDTO> atualizar(
            @PathVariable Long idUsuario,
            @PathVariable Long idPlanejamento,
            @Valid @RequestBody RevisaoSemanalRequestDTO dto
    ) {

        return ResponseEntity.ok(
                revisaoService.atualizar(
                        idUsuario,
                        idPlanejamento,
                        dto
                )
        );
    }

    @PatchMapping(
            "/planejamentos-semanais/{idPlanejamento}/revisao"
    )
    public ResponseEntity<RevisaoSemanalResponseDTO>
    atualizarParcialmente(
            @PathVariable Long idUsuario,
            @PathVariable Long idPlanejamento,
            @RequestBody RevisaoSemanalPatchDTO dto
    ) {

        return ResponseEntity.ok(
                revisaoService.atualizarParcialmente(
                        idUsuario,
                        idPlanejamento,
                        dto
                )
        );
    }

    @DeleteMapping(
            "/planejamentos-semanais/{idPlanejamento}/revisao"
    )
    public ResponseEntity<Void> excluir(
            @PathVariable Long idUsuario,
            @PathVariable Long idPlanejamento
    ) {

        revisaoService.excluir(
                idUsuario,
                idPlanejamento
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}