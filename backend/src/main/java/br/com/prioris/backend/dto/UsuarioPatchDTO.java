package br.com.prioris.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UsuarioPatchDTO {

    @Size(min = 1, max = 120,
            message = "O nome deve possuir entre 1 e 120 caracteres")
    private String nome;

    @Email(message = "Informe um e-mail válido")
    @Size(max = 254,
            message = "O e-mail deve possuir no máximo 254 caracteres")
    private String email;

    @Size(min = 8,
            message = "A senha deve possuir pelo menos 8 caracteres")
    private String senha;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}