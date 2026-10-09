package io.github.diegohahn.ado.security;

public class TokenEncryptionException extends RuntimeException {
    public TokenEncryptionException(String message) {
        super(message);
    }

    public TokenEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
