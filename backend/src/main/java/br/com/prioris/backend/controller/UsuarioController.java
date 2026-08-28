package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.UsuarioResponseDTO;
import br.com.prioris.backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.prioris.backend.dto.UsuarioRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import br.com.prioris.backend.dto.UsuarioAtualizacaoDTO;
import br.com.prioris.backend.dto.UsuarioPatchDTO;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
}