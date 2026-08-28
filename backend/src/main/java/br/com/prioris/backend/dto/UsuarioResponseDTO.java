package br.com.prioris.backend.dto;

import java.time.LocalDateTime;

public class UsuarioResponseDTO {

    private Long idUsuario;
    private String nome;
    private String email;
    private LocalDateTime dataCriacao;
    private Boolean ativo;

    public UsuarioResponseDTO(
            Long idUsuario,
            String nome,
            String email,
            LocalDateTime dataCriacao,
            Boolean ativo
    ) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.dataCriacao = dataCriacao;
        this.ativo = ativo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}