package id.ac.ui.cs.advprog.kost.occupancy.exceptions;

public class OccupancyFutureException extends RuntimeException {
    public OccupancyFutureException(String message) {
        super("Tenant asynchronous computation was " + message);
    }
}
