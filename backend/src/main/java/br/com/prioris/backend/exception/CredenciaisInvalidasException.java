package br.com.prioris.backend.exception;

public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException() {
        super("E-mail ou senha inválidos.");
    }

    public CredenciaisInvalidasException(String message) {
        super(message);
    }
}