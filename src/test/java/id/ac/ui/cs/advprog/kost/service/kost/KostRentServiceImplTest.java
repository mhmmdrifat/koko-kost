package id.ac.ui.cs.advprog.kost.service.kost;

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
import id.ac.ui.cs.advprog.kost.core.model.JwtPayload;
import id.ac.ui.cs.advprog.kost.rent.dto.KostRentRequest;
import id.ac.ui.cs.advprog.kost.rent.exceptions.KostRentDoesNotExistException;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;
import id.ac.ui.cs.advprog.kost.rent.repository.KostRentRepository;
import id.ac.ui.cs.advprog.kost.rent.service.KostRentServiceImpl;
import id.ac.ui.cs.advprog.kost.room.exceptions.KostRoomDoesNotExistException;
import id.ac.ui.cs.advprog.kost.room.exceptions.KostRoomOutOfStockException;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.model.KostRoomType;
import id.ac.ui.cs.advprog.kost.room.repository.KostRoomRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KostRentServiceImplTest {

    @InjectMocks
    private KostRentServiceImpl service;

    @Mock
    private KostRoomRepository roomRepository;
    @Mock
    private KostRentRepository rentRepository;

    List<GrantedAuthority> authorities = new ArrayList<>();

    KostRoom room;
    KostRent rent;
    KostRent newRent;
    KostRentRequest createRequest;
    KostRentRequest updateRequest;

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

        createRequest = KostRentRequest.builder()
                .userId(0)
                .userName("agun")
                .kostRoomId(0)
                .checkInDate((String) "2014-05-1")
                .checkOutDate((String) "2014-08-1")
                .duration(3)
                .totalPrice((double) 3600000)
                .build();

        rent = KostRent.builder()
                .id(0)
                .userId(0)
                .userName("agun")
                .kostRoom(room)
                .checkInDate(Util.parseDate("2014-05-01"))
                .checkOutDate(Util.parseDate("2014-08-1"))
                .duration(3)
                .totalPrice((double) 3600000)
                .build();

    }

    @Test
    void whenFindAllKostRentByTenantIdShouldReturnListOfKostRents() {
        List<KostRent> allRents = List.of(rent);
        var userId = 0;

        when(rentRepository.findAll()).thenReturn(allRents);

        List<KostRent> result = service.findAllByTenantId(userId);
        verify(rentRepository, atLeastOnce()).findAll();

        Assertions.assertEquals(allRents, result);
    }

    @Test
    void whenFindAllKostRentShouldReturnListOfKostRents() {
        List<KostRent> allRents = List.of(rent);

        when(rentRepository.findAll()).thenReturn(allRents);

        List<KostRent> result = service.findAll();
        verify(rentRepository, atLeastOnce()).findAll();
        Assertions.assertEquals(allRents, result);
    }

    @Test
    void whenFindByIdAndFoundShouldReturnKostRent() {
    when(rentRepository.findById(any(Integer.class))).thenReturn(Optional.of(rent));

    KostRent result = service.findById(0);
    verify(rentRepository, atLeastOnce()).findById(any(Integer.class));
    Assertions.assertEquals(rent, result);
    }

    @Test
    void whenFindByIdAndNotFoundShouldThrowException() {
        when(rentRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(KostRentDoesNotExistException.class, () -> {
            service.findById(0);
        });
    }

    @Test
    void whenCreateKostRentButRoomNotFoundShouldThrowException() {

        when(roomRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(KostRoomDoesNotExistException.class, () -> {
            service.create(createRequest);
        });

    }

    @Test
    void whenCreateKostRentAndRoomFoundShouldReturnNewKostRent() throws KostRoomDoesNotExistException {
        when(roomRepository.findById(any(Integer.class))).thenReturn(Optional.of(room));
        when(rentRepository.save(any(KostRent.class))).thenReturn(rent);

        KostRent result = service.create(createRequest);
        verify(roomRepository, atLeastOnce()).findById(any(Integer.class));
        verify(rentRepository, atLeastOnce()).save(any(KostRent.class));
        Assertions.assertEquals(rent, result);
    }

    @Test
    void whenDeleteKostRentAndFoundShouldCallDeleteByIdOnRepo() {
        when(rentRepository.findById(any(Integer.class))).thenReturn(Optional.of(rent));

        service.delete(0);
        verify(rentRepository, atLeastOnce()).deleteById(any(Integer.class));
    }

    @Test
    void whenDeleteKostRentAndNotFoundShouldThrowException() {
        when(rentRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(KostRentDoesNotExistException.class, () -> {
            service.delete(0);
        });
    }

    @Test
    void whenCreateKostRentAndCheckInDateAfterCheckOutDateShouldThrowException() throws KostRoomDoesNotExistException {
        KostRentRequest invalidRequest = KostRentRequest.builder()
                .userId(0)
                .kostRoomId(0)
                .checkInDate((String) "2014-08-1")
                .checkOutDate((String) "2014-05-1")
                .duration(3)
                .totalPrice((double) 3600000)
                .build();

        when(roomRepository.findById(any(Integer.class))).thenReturn(Optional.of(room));

        assertThrows(IllegalArgumentException.class, () -> {
            service.create(invalidRequest);
        });
    }

    @Test
    void whenCreateKostRentAndRoomFullyBookedShouldThrowException() throws KostRoomOutOfStockException {

        KostRentRequest fullyBookedRequest = KostRentRequest.builder()
                .userId(0)
                .kostRoomId(0)
                .checkInDate("2014-05-01")
                .checkOutDate("2014-08-01")
                .duration(3)
                .totalPrice((double) 3600000)
                .build();

        room.setStock(0);

        when(roomRepository.findById(any(Integer.class))).thenReturn(Optional.of(room));

        assertThrows(KostRoomOutOfStockException.class, () -> {
            service.create(fullyBookedRequest);
        });
    }

}