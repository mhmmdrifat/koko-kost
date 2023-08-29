package id.ac.ui.cs.advprog.kost.bundle.exceptions;

public class BundleDoesNotExistException extends RuntimeException {
    public BundleDoesNotExistException(Integer id) {
        super("Bundle with id " + id + " does not exist");
    }
}
