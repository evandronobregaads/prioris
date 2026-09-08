package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.auth.LoginRequestDTO;
import br.com.prioris.backend.dto.auth.LoginResponseDTO;
import br.com.prioris.backend.service.AutenticacaoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;


    public AutenticacaoController(
            AutenticacaoService autenticacaoService
    ) {

        this.autenticacaoService =
                autenticacaoService;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(

            @Valid
            @RequestBody
            LoginRequestDTO request

    ) {

        LoginResponseDTO response =
                autenticacaoService.login(
                        request
                );


        return ResponseEntity.ok(
                response
        );
    }
}
