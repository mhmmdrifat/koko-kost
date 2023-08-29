package id.ac.ui.cs.advprog.kost.teman_menginap.exceptions;
public class InvalidTemanMenginapTenantException extends RuntimeException {
    public InvalidTemanMenginapTenantException(Integer id) {
        super("User with id " + id + " is not a valid tenant");
    }
}
