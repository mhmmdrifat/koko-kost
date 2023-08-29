package id.ac.ui.cs.advprog.kost.service.bundle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import id.ac.ui.cs.advprog.kost.Util;
import id.ac.ui.cs.advprog.kost.bundle.exceptions.BundleDoesNotExistException;
import id.ac.ui.cs.advprog.kost.bundle.model.Bundle;
import id.ac.ui.cs.advprog.kost.bundle.repository.BundleRepository;
import id.ac.ui.cs.advprog.kost.core.model.JwtPayload;
import id.ac.ui.cs.advprog.kost.order.dto.BundleOrderRequest;
import id.ac.ui.cs.advprog.kost.order.exceptions.BundleOrderDoesNotExistException;
import id.ac.ui.cs.advprog.kost.order.model.BundleOrder;
import id.ac.ui.cs.advprog.kost.order.repository.BundleOrderRepository;
import id.ac.ui.cs.advprog.kost.order.service.BundleOrderServiceImpl;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.model.KostRoomType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BundleOrderServiceImplTest {

    @InjectMocks
    private BundleOrderServiceImpl service;

    @Mock
    private BundleOrderRepository repository;
    @Mock
    private BundleRepository bundleRepository;

    KostRoom room;
    BundleOrder order;
    Bundle bundle;
    Bundle newBundle;
    BundleOrderRequest createRequest;

    List<GrantedAuthority> authorities = new ArrayList<>();

    @BeforeEach
    void setUp() {
        authorities.add(new SimpleGrantedAuthority("PENGELOLA"));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                null,
                new JwtPayload(0, "PELANGGAN", "agun", true, (double) 2_000_000),
                authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        room = KostRoom.builder()
                .id(0)
                .name("Campingski Resort")
                .type(KostRoomType.CAMPUR)
                .city("Jakarta")
                .country("Indonesia")
                .address("Jl Kebenaran")
                .facilities(new String[] { "Kamar mandi", "AC" })
                .images(new String[] {
                        "https://res.cloudinary.com/dkg0oswii/image/upload/v1669102647/cld-sample-5.jpg" })
                .stock(2)
                .price((double) 1200000)
                .discount(12)
                .minDiscountDuration(12)
                .build();

        createRequest = BundleOrderRequest.builder()
                .bundleId(0)
                .checkInDate(Util.parseDate("2023-01-1"))
                .checkOutDate(Util.parseDate("2023-06-1"))
                .build();

        bundle = Bundle.builder()
                .id(0)
                .name("Campingski Bundle")
                .kostRoom(room)
                .coworkingId(0)
                .duration(18)
                .bundlePrice((double) 11_000_000)
                .build();
        order = BundleOrder.builder()
                .id(0)
                .userId(0)
                .bundle(bundle)
                .checkInDate(Util.parseDate("2023-01-1"))
                .checkOutDate(Util.parseDate("2023-06-1"))
                .build();

        bundleRepository.save(bundle);

    }

    @Test
    void whenFindAllBundleOrderShouldReturnListOfBundleOrders() {
        List<BundleOrder> allBundles = List.of(order);

        when(repository.findAll()).thenReturn(allBundles);

        List<BundleOrder> result = service.findAll();
        verify(repository, atLeastOnce()).findAll();
        Assertions.assertEquals(allBundles, result);
    }

    @Test
    void whenFindAllBundleOrderByTenantIdShouldReturnListOfBundleOrders() {
        List<BundleOrder> allBundles = List.of(order);

        when(repository.findAll()).thenReturn(allBundles);

        List<BundleOrder> result = service.findAllByTenantId(0);

        verify(repository, atLeastOnce()).findAll();
        Assertions.assertEquals(allBundles, result);
    }

    @Test
    void whenFindByIdAndFoundShouldReturnBundleOrder() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(order));

        BundleOrder result = service.findById(0);
        verify(repository, atLeastOnce()).findById(any(Integer.class));
        Assertions.assertEquals(order, result);
    }

    @Test
    void whenFindByIdAndNotFoundShouldThrowException() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(BundleOrderDoesNotExistException.class, () -> {
            service.findById(0);
        });
    }

    @Test
    void whenDeleteBundleAndFoundShouldCallDeleteByIdOnRepo() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(order));

        service.delete(0);
        verify(repository, atLeastOnce()).deleteById(any(Integer.class));
    }

    @Test
    void whenDeleteBundleAndNotFoundShouldThrowException() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(BundleOrderDoesNotExistException.class, () -> {
            service.delete(0);
        });
    }

    @Test
    void whenCreateBundleOrderShouldReturnCreatedOrder() {
        when(bundleRepository.findById(anyInt())).thenReturn(Optional.of(bundle));
        when(repository.save(any(BundleOrder.class))).thenReturn(order);

        BundleOrder result = service.create(createRequest);

        verify(bundleRepository, atLeastOnce()).findById(anyInt());
        verify(repository, atLeastOnce()).save(any(BundleOrder.class));

        Assertions.assertEquals(order, result);
    }

    @Test
    void whenCreateBundleOrderAndBundleNotFoundShouldThrowException() {
        when(bundleRepository.findById(anyInt())).thenReturn(Optional.empty());

        Assertions.assertThrows(BundleDoesNotExistException.class, () -> {
            service.create(createRequest);
        });

        verify(bundleRepository, atLeastOnce()).findById(anyInt());
        verify(repository, never()).save(any(BundleOrder.class));
    }

    @Test
    void whenCheckHasCheckoutShouldReturnTrueWhenCheckOutDateIsPast() {
        BundleOrder order = BundleOrder.builder()
                .id(0)
                .userId(0)
                .bundle(bundle)
                .checkInDate(Util.parseDate("2023-01-01"))
                .checkOutDate(Util.parseDate("2023-04-01"))
                .build();

        boolean result = order.getHasCheckout();

        Assertions.assertTrue(result);
    }

    @Test
    void whenCheckHasCheckoutShouldReturnFalseWhenCheckOutDateIsFuture() {
        BundleOrder order = BundleOrder.builder()
                .id(0)
                .userId(0)
                .bundle(bundle)
                .checkInDate(Util.parseDate("2023-06-01"))
                .checkOutDate(Util.getTommorow())
                .build();

        boolean result = order.getHasCheckout();

        Assertions.assertFalse(result);
    }

}