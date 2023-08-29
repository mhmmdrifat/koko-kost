package id.ac.ui.cs.advprog.kost.order.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.kost.bundle.exceptions.BundleDoesNotExistException;
import id.ac.ui.cs.advprog.kost.bundle.exceptions.BundleOutOfStockException;

import id.ac.ui.cs.advprog.kost.bundle.repository.BundleRepository;
import id.ac.ui.cs.advprog.kost.core.model.JwtPayload;
import id.ac.ui.cs.advprog.kost.order.dto.BundleOrderRequest;
import id.ac.ui.cs.advprog.kost.order.exceptions.BundleOrderDoesNotExistException;
import id.ac.ui.cs.advprog.kost.order.model.BundleOrder;
import id.ac.ui.cs.advprog.kost.order.repository.BundleOrderRepository;
import id.ac.ui.cs.advprog.kost.rent.exceptions.InvalidTenantException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BundleOrderServiceImpl implements BundleOrderService {
    private final BundleOrderRepository bundleOrderRepository;
    private final BundleRepository bundleRepository;

    @Override
    public List<BundleOrder> findAll() {
        CompletableFuture<List<BundleOrder>> future = CompletableFuture
                .supplyAsync(bundleOrderRepository::findAll);
        return future.join();
    }

    @Override
    public List<BundleOrder> findAllByTenantId(Integer id) {
        CompletableFuture<List<BundleOrder>> future = CompletableFuture.supplyAsync(
                () -> bundleOrderRepository.findAll().stream().filter(rent -> rent.getUserId().equals(id)).toList());
        return future.join();
    }

    @Override
    public BundleOrder findById(Integer id) {

        if (isBundleOrderNotExist(id)) {
            throw new BundleOrderDoesNotExistException(id);
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = ((JwtPayload) authentication.getCredentials()).getUserId();

        CompletableFuture<BundleOrder> future = CompletableFuture.supplyAsync(() -> {
            BundleOrder order = bundleOrderRepository.findById(id).orElse(null);
            if (userId == null || !order.getUserId().equals(userId)) {
                throw new InvalidTenantException(userId);
            }

            return order;
        });
        return future.join();
    }

    @Override
    public synchronized BundleOrder create(BundleOrderRequest request) {
        var id = request.getBundleId();
        var bundle = bundleRepository.findById(id).orElse(null);
        if (bundle == null) {
            throw new BundleDoesNotExistException(id);
        }
        if (Boolean.TRUE.equals(bundle.getIsAvailable())) {
            var kost = bundle.getKostRoom();
            var bundleOrder = BundleOrder.builder()
                    .userId(request.getUserId())
                    .bundle(bundle)
                    .checkInDate(request.getCheckInDate())
                    .checkOutDate(request.getCheckOutDate())
                    .build();

            kost.setStock(kost.getStock() - 1);
            return this.bundleOrderRepository.save(bundleOrder);

        } else {
            throw new BundleOutOfStockException(id);
        }
    }

    @Override
    public void delete(Integer id) {
        if (isBundleOrderNotExist(id)) {
            throw new BundleOrderDoesNotExistException(id);
        }
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> bundleOrderRepository.deleteById(id));
        future.join();
    }

    private boolean isBundleOrderNotExist(Integer id) {
        return bundleOrderRepository.findById(id).isEmpty();
    }

}
