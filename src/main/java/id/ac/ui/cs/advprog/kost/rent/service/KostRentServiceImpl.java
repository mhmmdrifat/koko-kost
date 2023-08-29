package id.ac.ui.cs.advprog.kost.rent.service;

import java.util.List;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.kost.core.model.JwtPayload;
import id.ac.ui.cs.advprog.kost.core.util.Util;
import id.ac.ui.cs.advprog.kost.rent.dto.KostRentRequest;
import id.ac.ui.cs.advprog.kost.rent.exceptions.InvalidTenantException;
import id.ac.ui.cs.advprog.kost.rent.exceptions.KostRentDoesNotExistException;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;
import id.ac.ui.cs.advprog.kost.rent.repository.KostRentRepository;
import id.ac.ui.cs.advprog.kost.room.exceptions.KostRoomDoesNotExistException;
import id.ac.ui.cs.advprog.kost.room.exceptions.KostRoomOutOfStockException;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.repository.KostRoomRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KostRentServiceImpl implements KostRentService {
    private final KostRentRepository kostRentRepository;
    private final KostRoomRepository kostRoomRepository;

    @Override
    public List<KostRent> findAll() {
        CompletableFuture<List<KostRent>> future = CompletableFuture.supplyAsync(kostRentRepository::findAll);
        return future.join();
    }

    @Override
    public List<KostRent> findAllByTenantId(Integer id) {
        CompletableFuture<List<KostRent>> future = CompletableFuture.supplyAsync(
                () -> kostRentRepository.findAll().stream().filter(rent -> rent.getUserId().equals(id)).toList());
        return future.join();

    }

    @Override
    public KostRent findById(Integer id) {
        if (isKostRentNotExist(id)) {
            throw new KostRentDoesNotExistException(id);
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        Integer userId = ((JwtPayload) authentication.getCredentials()).getUserId();
        CompletableFuture<KostRent> future = CompletableFuture.supplyAsync(() -> {
            KostRent rent = kostRentRepository.findById(id).orElse(null);
            if (userId == null || !rent.getUserId().equals(userId)) {
                throw new InvalidTenantException(userId);
            }

            return rent;
        });
        return future.join();
    }

    @Override
    public synchronized KostRent create(KostRentRequest request) {
        var id = request.getKostRoomId();

        var kostRoomOptional = kostRoomRepository.findById(id);
        Date checkInDate = Util.parseDate(request.getCheckInDate());
        Date checkOutDate = Util.parseDate(request.getCheckOutDate());
        if (kostRoomOptional.isEmpty()) {
            throw new KostRoomDoesNotExistException(id);
        }
        KostRoom room = kostRoomOptional.orElse(null);
        Integer stock = room.getStock();
        if ((checkInDate).after(checkOutDate)) {
            throw new IllegalArgumentException();
        }
        if (stock > 0) {
            var kostRent = KostRent.builder()
                    .userId(request.getUserId())
                    .userName(request.getUserName())
                    .kostRoom(room)
                    .roomNumber(stock)
                    .checkInDate(checkInDate)
                    .checkOutDate(checkOutDate)
                    .duration(request.getDuration())
                    .totalPrice(Math.round(request.getTotalPrice() * 100.0) / 100.0)
                    .build();

            room.setStock(room.getStock() - 1);
            return this.kostRentRepository.save(kostRent);
        } else {
            throw new KostRoomOutOfStockException(id);
        }

    }

    @Override
    public void delete(Integer id) {
        if (isKostRentNotExist(id)) {
            throw new KostRentDoesNotExistException(id);
        }
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> kostRentRepository.deleteById(id));
        future.join();
    }

    private boolean isKostRentNotExist(Integer id) {
        return kostRentRepository.findById(id).isEmpty();
    }

}
