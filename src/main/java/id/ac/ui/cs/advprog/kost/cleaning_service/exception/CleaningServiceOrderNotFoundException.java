package id.ac.ui.cs.advprog.kost.cleaning_service.exception;

public class CleaningServiceOrderNotFoundException extends RuntimeException {
    public CleaningServiceOrderNotFoundException(Integer id) {
        super("Service order with id " + id + " does not exist");
    }
}

