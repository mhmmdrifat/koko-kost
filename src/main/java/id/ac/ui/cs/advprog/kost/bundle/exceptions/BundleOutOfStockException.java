package id.ac.ui.cs.advprog.kost.bundle.exceptions;

public class BundleOutOfStockException extends RuntimeException {
    public BundleOutOfStockException(Integer id) {
        super("Bundle with id " + id + " can't be purchased");
    }
}
