package id.ac.ui.cs.advprog.kost.bundle.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.kost.bundle.dto.BundleRequest;
import id.ac.ui.cs.advprog.kost.bundle.exceptions.BundleDoesNotExistException;
import id.ac.ui.cs.advprog.kost.bundle.model.Bundle;
import id.ac.ui.cs.advprog.kost.bundle.repository.BundleRepository;
import id.ac.ui.cs.advprog.kost.room.exceptions.KostRoomDoesNotExistException;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.repository.KostRoomRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BundleServiceImpl implements BundleService {
    private final BundleRepository bundleRepository;
    private final KostRoomRepository kostRoomRepository;

    @Override
    public List<Bundle> findAll() {
        CompletableFuture<List<Bundle>> future = CompletableFuture.supplyAsync(bundleRepository::findAll);
        return future.join();
    }

    @Override
    public Bundle findById(Integer id) {
        if (isBundleNotExist(id)) {
            throw new BundleDoesNotExistException(id);
        }
        CompletableFuture<Bundle> future = CompletableFuture
                .supplyAsync(() -> bundleRepository.findById(id).orElse(null));
        return future.join();

    }

    @Override
    public Bundle create(BundleRequest request) {
        var id = request.getKostRoomId();
        if (kostRoomRepository.findById(id).isEmpty()) {
            throw new KostRoomDoesNotExistException(id);
        }
        KostRoom room = kostRoomRepository.findById(id).orElse(null);
        var bundle = Bundle.builder()
                .name(request.getName())
                .kostRoom(room)
                .coworkingId(request.getCoworkingId())
                .duration(request.getDuration())
                .bundlePrice(request.getBundlePrice())
                .build();

        return this.bundleRepository.save(bundle);
    }

    @Override
    public void delete(Integer id) {

        if (isBundleNotExist(id)) {
            throw new BundleDoesNotExistException(id);
        }
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> bundleRepository.deleteById(id));
        future.join();
    }

    private boolean isBundleNotExist(Integer id) {
        return bundleRepository.findById(id).isEmpty();
    }

}
