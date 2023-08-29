package id.ac.ui.cs.advprog.kost.room.exceptions;

public class KostRoomDoesNotExistException extends RuntimeException {
    public KostRoomDoesNotExistException(Integer id) {
        super("Kost Room with id " + id + " does not exist");
    }
}
