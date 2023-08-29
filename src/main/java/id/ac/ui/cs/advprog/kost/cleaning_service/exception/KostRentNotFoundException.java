package id.ac.ui.cs.advprog.kost.cleaning_service.exception;

public class KostRentNotFoundException extends RuntimeException {
    public KostRentNotFoundException(Integer kostRentId) {
        super("KostRent with ID " + kostRentId + " not found.");
    }
}