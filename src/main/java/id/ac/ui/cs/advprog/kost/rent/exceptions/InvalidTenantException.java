package id.ac.ui.cs.advprog.kost.rent.exceptions;

public class InvalidTenantException extends RuntimeException {
    public InvalidTenantException(Integer id) {
        super("User with id " + id + " is not a valid tenant");
    }
}
