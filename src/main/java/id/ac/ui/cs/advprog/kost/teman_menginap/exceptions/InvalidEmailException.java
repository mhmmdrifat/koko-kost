package id.ac.ui.cs.advprog.kost.teman_menginap.exceptions;

public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(String email) {
        super("Email is invalid: " + email);
    }
}
