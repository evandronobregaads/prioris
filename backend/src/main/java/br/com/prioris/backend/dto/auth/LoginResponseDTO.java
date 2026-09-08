package br.com.prioris.backend.dto.auth;

public record LoginResponseDTO(

        Long idUsuario,
        String nome,
        String email

) {
}