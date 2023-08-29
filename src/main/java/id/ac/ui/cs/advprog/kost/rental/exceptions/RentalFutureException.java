package id.ac.ui.cs.advprog.kost.rental.exceptions;

public class RentalFutureException extends RuntimeException {
    public RentalFutureException(String message) {
        super("Rental asynchronous computation was " + message);
    }
}
