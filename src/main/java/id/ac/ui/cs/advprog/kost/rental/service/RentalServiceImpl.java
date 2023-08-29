package id.ac.ui.cs.advprog.kost.rental.service;

import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOrder;
import id.ac.ui.cs.advprog.kost.cleaning_service.repository.CleaningServiceOrderRepository;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;
import id.ac.ui.cs.advprog.kost.rent.repository.KostRentRepository;
import id.ac.ui.cs.advprog.kost.rental.dto.Rental;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import id.ac.ui.cs.advprog.kost.rental.exceptions.RentalFutureException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final KostRentRepository kostRentRepository;
    private final CleaningServiceOrderRepository roomServiceRepository;

    private CompletableFuture<List<KostRent>> getAsyncActiveRentsByUserId(Integer userId) {
        return CompletableFuture.supplyAsync(() -> kostRentRepository.findAll().stream()
                .filter(rent -> rent != null && rent.getUserId().equals(userId) && !rent.getHasCheckout())
                .toList());
    }

    private CompletableFuture<List<CleaningServiceOrder>> getAsyncRoomServicesByUserId(Integer userId) {
        return CompletableFuture.supplyAsync(() -> roomServiceRepository.findAll().stream()
                .filter(service -> service != null && service.getUserId().equals(userId))
                .toList());
    }

    private <T> List<T> getDataFromFuture(CompletableFuture<List<T>> dataFuture) {
        try {
            return dataFuture.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RentalFutureException("interrupted while waiting for the result");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                throw new RentalFutureException("interrupted while waiting for the result");
            } else {
                throw new RentalFutureException("executed with error: " + cause.getMessage());
            }
        }
    }

    @Override
    public List<Rental> findAllByUserId(Integer userId) {
        CompletableFuture<List<KostRent>> activeRentsFuture = getAsyncActiveRentsByUserId(userId);
        CompletableFuture<List<CleaningServiceOrder>> roomServicesFuture = getAsyncRoomServicesByUserId(userId);
        CompletableFuture.allOf(activeRentsFuture, roomServicesFuture);

        List<KostRent> activeRents = getDataFromFuture(activeRentsFuture);
        List<CleaningServiceOrder> roomServices = getDataFromFuture(roomServicesFuture);

        return Rental.createRentals(activeRents, roomServices);
    }
}
