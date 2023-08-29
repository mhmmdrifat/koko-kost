package id.ac.ui.cs.advprog.kost.core.exceptions;

public class InvalidDateException extends RuntimeException {
    public InvalidDateException(String fieldName) {
        super(fieldName + " must be in the format yyyy-MM-dd");
    }
}
