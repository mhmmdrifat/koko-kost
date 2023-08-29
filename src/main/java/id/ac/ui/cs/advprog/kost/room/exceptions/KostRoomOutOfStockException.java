package id.ac.ui.cs.advprog.kost.room.exceptions;

public class KostRoomOutOfStockException extends RuntimeException {
    public KostRoomOutOfStockException(Integer id) {
        super("Kost Room with id " + id + " can't be rent");
    }
}
