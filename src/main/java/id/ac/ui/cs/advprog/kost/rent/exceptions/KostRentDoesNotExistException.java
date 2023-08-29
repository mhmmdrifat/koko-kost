package id.ac.ui.cs.advprog.kost.rent.exceptions;

public class KostRentDoesNotExistException extends RuntimeException {
    public KostRentDoesNotExistException(Integer id) {
        super("Kost Rent with id " + id + " does not exist");
    }
}
