package id.ac.ui.cs.advprog.kost.core.exceptions;

public class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException() {
        super("Could not verify JWT token integrity!");
    }
}
