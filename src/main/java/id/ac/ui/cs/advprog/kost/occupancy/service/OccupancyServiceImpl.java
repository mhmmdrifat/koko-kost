package id.ac.ui.cs.advprog.kost.occupancy.service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import id.ac.ui.cs.advprog.kost.cleaning_service.repository.CleaningServiceOrderRepository;
import id.ac.ui.cs.advprog.kost.rent.repository.KostRentRepository;
import id.ac.ui.cs.advprog.kost.room.repository.KostRoomRepository;
import id.ac.ui.cs.advprog.kost.occupancy.dto.Tenant;
import id.ac.ui.cs.advprog.kost.occupancy.dto.WithoutTenant;
import id.ac.ui.cs.advprog.kost.occupancy.exceptions.OccupancyFilterException;
import id.ac.ui.cs.advprog.kost.occupancy.exceptions.OccupancyFutureException;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOrder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OccupancyServiceImpl implements OccupancyService {
    private final KostRentRepository kostRentRepository;
    private final CleaningServiceOrderRepository roomServiceRepository;
    private final KostRoomRepository kostRoomRepository;

    private CompletableFuture<List<KostRent>> getAsyncActiveRents() {
        return CompletableFuture.supplyAsync(() ->
                kostRentRepository.findAll().stream()
                        .filter(rent -> rent != null && !rent.getHasCheckout())
                        .toList()
        );
    }

    private CompletableFuture<List<CleaningServiceOrder>> getAsyncRoomServices() {
        return CompletableFuture.supplyAsync(() ->
                roomServiceRepository.findAll().stream()
                        .filter(Objects::nonNull)
                        .toList()
        );
    }

    private CompletableFuture<List<KostRent>> getAsyncActiveRentsByRoomId(Integer roomId) {
        return CompletableFuture.supplyAsync(() ->
                kostRentRepository.findAll().stream()
                        .filter(rent -> rent != null && !rent.getHasCheckout()
                                && rent.getKostRoom().getId().equals(roomId))
                        .toList()
        );
    }

    private CompletableFuture<List<CleaningServiceOrder>> getAsyncRoomServicesByRoomId(Integer roomId) {
        return CompletableFuture.supplyAsync(() ->
                roomServiceRepository.findAll().stream()
                        .filter(service -> service != null &&
                                Objects.equals(service.getKostRent().getKostRoom().getId(), roomId))
                        .toList()
        );
    }

    private <T> List<T> getDataFromFuture(CompletableFuture<List<T>> dataFuture) {
        try {
            return dataFuture.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OccupancyFutureException("interrupted while waiting for the result");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                throw new OccupancyFutureException("interrupted while waiting for the result");
            } else {
                throw new OccupancyFutureException("executed with error: " + cause.getMessage());
            }
        }
    }

    private KostRoom getKostRoomByRoomName(String roomName) {
        var kostRoom = kostRoomRepository.findByNameIgnoreCase(roomName).orElse(null);

        if (kostRoom == null) {
            throw new OccupancyFilterException(roomName);
        }
        return kostRoom;
    }

    public List<Tenant> findAllTenant() {
        CompletableFuture<List<KostRent>> activeRentsFuture = getAsyncActiveRents();
        CompletableFuture<List<CleaningServiceOrder>> roomServicesFuture = getAsyncRoomServices();
        CompletableFuture.allOf(activeRentsFuture, roomServicesFuture);

        List<KostRent> activeRents = getDataFromFuture(activeRentsFuture);
        List<CleaningServiceOrder> roomServices = getDataFromFuture(roomServicesFuture);

        return Tenant.createTenants(activeRents, roomServices);
    }

    public List<Tenant> findAllTenantByRoomName(String roomName) {
        var kostRoom = getKostRoomByRoomName(roomName);

        CompletableFuture<List<KostRent>> activeRentsFuture = getAsyncActiveRentsByRoomId(kostRoom.getId());
        CompletableFuture<List<CleaningServiceOrder>> roomServicesFuture = getAsyncRoomServicesByRoomId(kostRoom.getId());
        CompletableFuture.allOf(activeRentsFuture, roomServicesFuture);

        List<KostRent> activeRents = getDataFromFuture(activeRentsFuture);
        List<CleaningServiceOrder> roomServices = getDataFromFuture(roomServicesFuture);

        return Tenant.createTenants(activeRents, roomServices);
    }

    public List<WithoutTenant> findAllWithoutTenant() {
        final var id = new int[]{0};

        return kostRoomRepository.findAll().stream()
                .filter(KostRoom::getIsAvailable)
                .flatMap(availableRoom -> WithoutTenant.createWithoutTenants(availableRoom, id).stream())
                .toList();

    }

    public List<WithoutTenant> findAllWithoutTenantByRoomName(String roomName) {
        var kostRoom = getKostRoomByRoomName(roomName);
        return WithoutTenant.createWithoutTenants(kostRoom, new int[]{0});
    }
}