package id.ac.ui.cs.advprog.kost.occupancy.exceptions;

public class OccupancyFilterException extends RuntimeException {
    public OccupancyFilterException(String roomName) {
        super("There are no Kost Room with the name " + roomName);
    }
}
