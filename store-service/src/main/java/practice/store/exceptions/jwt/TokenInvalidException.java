package practice.store.exceptions.jwt;

public class TokenInvalidException extends RuntimeException {

    public TokenInvalidException() {
        super("Token is invalid.");
    }
}