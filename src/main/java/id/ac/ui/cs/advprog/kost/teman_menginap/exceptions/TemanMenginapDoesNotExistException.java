package id.ac.ui.cs.advprog.kost.teman_menginap.exceptions;

public class TemanMenginapDoesNotExistException extends RuntimeException {
    public TemanMenginapDoesNotExistException(int id) {
        super("Teman Menginap not found with id:" + id);
    }
}
