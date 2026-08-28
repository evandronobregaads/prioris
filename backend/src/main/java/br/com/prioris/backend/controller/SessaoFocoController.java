package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.SessaoFocoFinalizacaoDTO;
import br.com.prioris.backend.dto.SessaoFocoRequestDTO;
import br.com.prioris.backend.dto.SessaoFocoResponseDTO;
import br.com.prioris.backend.service.SessaoFocoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}/sessoes-foco")
public class SessaoFocoController {

    private final SessaoFocoService sessaoFocoService;

    public SessaoFocoController(
            SessaoFocoService sessaoFocoService
    ) {
        this.sessaoFocoService = sessaoFocoService;
    }

    @PostMapping
    public ResponseEntity<SessaoFocoResponseDTO> iniciar(
            @PathVariable Long idUsuario,
            @Valid @RequestBody SessaoFocoRequestDTO dto
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        sessaoFocoService.iniciar(
                                idUsuario,
                                dto
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<SessaoFocoResponseDTO>>
    listarHistorico(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                sessaoFocoService.listarHistorico(idUsuario)
        );
    }

    @GetMapping("/{idSessao}")
    public ResponseEntity<SessaoFocoResponseDTO> buscarPorId(
            @PathVariable Long idUsuario,
            @PathVariable Long idSessao
    ) {

        return ResponseEntity.ok(
                sessaoFocoService.buscarPorId(
                        idUsuario,
                        idSessao
                )
        );
    }

    @PatchMapping("/{idSessao}/pausar")
    public ResponseEntity<SessaoFocoResponseDTO> pausar(
            @PathVariable Long idUsuario,
            @PathVariable Long idSessao
    ) {

        return ResponseEntity.ok(
                sessaoFocoService.pausar(
                        idUsuario,
                        idSessao
                )
        );
    }

    @PatchMapping("/{idSessao}/retomar")
    public ResponseEntity<SessaoFocoResponseDTO> retomar(
            @PathVariable Long idUsuario,
            @PathVariable Long idSessao
    ) {

        return ResponseEntity.ok(
                sessaoFocoService.retomar(
                        idUsuario,
                        idSessao
                )
        );
    }

    @PatchMapping("/{idSessao}/finalizar")
    public ResponseEntity<SessaoFocoResponseDTO> finalizar(
            @PathVariable Long idUsuario,
            @PathVariable Long idSessao,
            @Valid @RequestBody SessaoFocoFinalizacaoDTO dto
    ) {

        return ResponseEntity.ok(
                sessaoFocoService.finalizar(
                        idUsuario,
                        idSessao,
                        dto
                )
        );
    }

    @PatchMapping("/{idSessao}/interromper")
    public ResponseEntity<SessaoFocoResponseDTO> interromper(
            @PathVariable Long idUsuario,
            @PathVariable Long idSessao,
            @Valid @RequestBody SessaoFocoFinalizacaoDTO dto
    ) {

        return ResponseEntity.ok(
                sessaoFocoService.interromper(
                        idUsuario,
                        idSessao,
                        dto
                )
        );
    }
}