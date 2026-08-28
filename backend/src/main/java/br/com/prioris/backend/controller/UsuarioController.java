package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import br.com.prioris.backend.service.ObjetivoService;

import br.com.prioris.backend.dto.ObjetivoRequestDTO;
import br.com.prioris.backend.dto.ObjetivoResponseDTO;
import br.com.prioris.backend.service.ObjetivoService;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ObjetivoService objetivoService;

    public UsuarioController(
            UsuarioService usuarioService,
            ObjetivoService objetivoService
    ) {
        this.usuarioService = usuarioService;
        this.objetivoService = objetivoService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(
            @Valid @RequestBody UsuarioRequestDTO dto
    ) {

        UsuarioResponseDTO usuario = usuarioService.cadastrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioAtualizacaoDTO dto
    ) {

        return ResponseEntity.ok(
                usuarioService.atualizar(id, dto)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarParcialmente(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioPatchDTO dto
    ) {

        return ResponseEntity.ok(
                usuarioService.atualizarParcialmente(id, dto)
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {

        usuarioService.excluir(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/objetivos")
    public ResponseEntity<ObjetivoResponseDTO> cadastrarObjetivo(
            @PathVariable Long id,
            @Valid @RequestBody ObjetivoRequestDTO dto
    ) {

        ObjetivoResponseDTO objetivo =
                objetivoService.cadastrar(id, dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(objetivo);
    }
}