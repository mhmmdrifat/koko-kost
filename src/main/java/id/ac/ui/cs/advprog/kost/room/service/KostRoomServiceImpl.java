package id.ac.ui.cs.advprog.kost.room.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.kost.room.dto.KostRoomRequest;
import id.ac.ui.cs.advprog.kost.room.exceptions.KostRoomDoesNotExistException;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.model.KostRoomType;
import id.ac.ui.cs.advprog.kost.room.repository.KostRoomRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KostRoomServiceImpl implements KostRoomService {
    private final KostRoomRepository kostRoomRepository;

    @Override
    public List<KostRoom> findAll() {
        CompletableFuture<List<KostRoom>> future = CompletableFuture.supplyAsync(kostRoomRepository::findAll);
        return future.join();
    }

    @Override
    public KostRoom findById(Integer id) {
        if (isKostRoomNotExist(id)) {
            throw new KostRoomDoesNotExistException(id);
        }
        CompletableFuture<KostRoom> future = CompletableFuture
                .supplyAsync(() -> kostRoomRepository.findById(id).orElse(null));
        return future.join();
    }

    @Override
    public KostRoom create(KostRoomRequest request) {
        CompletableFuture<KostRoom> future = CompletableFuture.supplyAsync(() -> {
            var kostRoom = KostRoom.builder()
                    .name(request.getName())
                    .type(KostRoomType.valueOf(request.getType()))
                    .city(request.getCity())
                    .country(request.getCountry())
                    .address(request.getAddress())
                    .facilities(request.getFacilities())
                    .images(request.getImages())
                    .stock(request.getStock())
                    .price(request.getPrice())
                    .discount(request.getDiscount())
                    .minDiscountDuration(request.getMinDiscountDuration())
                    .build();

            return kostRoomRepository.save(kostRoom);
        });
        return future.join();
    }

    @Override
    public KostRoom update(Integer id, KostRoomRequest request) {
        if (isKostRoomNotExist(id)) {
            throw new KostRoomDoesNotExistException(id);
        }
        CompletableFuture<KostRoom> future = CompletableFuture.supplyAsync(() -> {
            KostRoom currentRoom = kostRoomRepository.findById(id).orElse(null);
            var kostRoom = KostRoom.builder()
                    .id(id)
                    .name(request.getName())
                    .type(KostRoomType.valueOf(request.getType()))
                    .city(currentRoom.getCity())
                    .country(currentRoom.getCountry())
                    .address(currentRoom.getAddress())
                    .facilities(request.getFacilities())
                    .images(currentRoom.getImages())
                    .stock(request.getStock())
                    .price(request.getPrice())
                    .discount(request.getDiscount())
                    .minDiscountDuration(request.getMinDiscountDuration())
                    .build();

            return kostRoomRepository.save(kostRoom);
        });
        return future.join();
    }

    @Override
    public void delete(Integer id) {
        if (isKostRoomNotExist(id)) {
            throw new KostRoomDoesNotExistException(id);
        }
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> kostRoomRepository.deleteById(id));
        future.join();
    }

    private boolean isKostRoomNotExist(Integer id) {
        return kostRoomRepository.findById(id).isEmpty();
    }

}