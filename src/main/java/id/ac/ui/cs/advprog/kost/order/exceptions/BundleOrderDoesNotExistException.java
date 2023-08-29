package id.ac.ui.cs.advprog.kost.order.exceptions;

public class BundleOrderDoesNotExistException extends RuntimeException {
    public BundleOrderDoesNotExistException(Integer id) {
        super("Bundle Order with id " + id + " does not exist");
    }
}
