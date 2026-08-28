package br.com.prioris.backend.exception;

public class RevisaoSemanalJaExisteException
        extends RuntimeException {

    public RevisaoSemanalJaExisteException(String mensagem) {
        super(mensagem);
    }
}