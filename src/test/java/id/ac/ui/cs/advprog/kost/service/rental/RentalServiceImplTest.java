package id.ac.ui.cs.advprog.kost.service.rental;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOrder;
import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceStatus;
import id.ac.ui.cs.advprog.kost.cleaning_service.repository.CleaningServiceOrderRepository;
import id.ac.ui.cs.advprog.kost.rental.exceptions.RentalFutureException;
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
import id.ac.ui.cs.advprog.kost.rental.service.RentalServiceImpl;
import id.ac.ui.cs.advprog.kost.rent.repository.KostRentRepository;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.model.KostRoomType;
import id.ac.ui.cs.advprog.kost.rental.dto.Rental;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {

    @Mock
    private KostRentRepository kostRentRepository;
    
    @Mock
    private CleaningServiceOrderRepository roomServiceRepository;
    
    @InjectMocks
    private RentalServiceImpl rentalService;

    List<GrantedAuthority> authorities = new ArrayList<>();

 
    KostRent kostRent1;
    KostRent kostRent2;
    KostRent kostRent3;
    KostRoom kostRoom;
    CleaningServiceOrder roomService1;
    CleaningServiceOrder roomService2;

    @BeforeEach
    void setUp() {
        authorities.add(new SimpleGrantedAuthority("PENGELOLA"));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                null,
                new JwtPayload(0, "PELANGGAN", "Agun", true, (double) 2_000_000),
                authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        kostRoom = KostRoom.builder()
                .id(0)
                .name("Campingski Resort")
                .type(KostRoomType.CAMPUR)
                .city("Jakarta")
                .country("Indonesia")
                .address("Jl Kebenaran")
                .facilities(new String[] { "Kamar mandi", "AC" })
                .images(new String[] {
                        "https://res.cloudinary.com/dkg0oswii/image/upload/v1669102647/cld-sample-4.jpg" })
                .stock(2)
                .price((double) 1200000)
                .discount(12)
                .minDiscountDuration(12)
                .build();

        kostRent1 = KostRent.builder()
                .id(0)
                .userId(0)
                .roomNumber(3)
                .userName("Agun")
                .kostRoom(kostRoom)
                .checkInDate(Util.parseDate("2015-05-10"))
                .checkOutDate(Util.parseDate("2015-08-10"))
                .duration(3)
                .totalPrice((double) 3600000)
                .build();

        kostRent2 = KostRent.builder()
                .id(1)
                .userId(0)
                .userName("Agun")
                .roomNumber(4)
                .kostRoom(kostRoom)
                .checkInDate(Util.parseDate("2023-03-11"))
                .checkOutDate(Util.parseDate("2023-06-11"))
                .duration(3)
                .totalPrice((double) 3600000)
                .build();

        kostRent3 = KostRent.builder()
                .id(2)
                .userId(0)
                .userName("Agun")
                .roomNumber(5)
                .kostRoom(kostRoom)
                .checkInDate(Util.parseDate("2023-03-12"))
                .checkOutDate(Util.parseDate("2023-06-12"))
                .duration(3)
                .totalPrice((double) 3600000)
                .build();

        roomService1 = CleaningServiceOrder.builder()
                .id(0)
                .UserId(0)
                .kostRent(kostRent1)
                .startDate(Util.parseDate("2015-06-05"))
                .endDate(Util.parseDate("2015-06-05"))
                .status(CleaningServiceStatus.PENDING)
                .build();

        roomService2 = CleaningServiceOrder.builder()
                .id(1)
                .UserId(0)
                .kostRent(kostRent2)
                .startDate(Util.parseDate("2023-05-05"))
                .endDate(Util.parseDate("2023-05-05"))
                .status(CleaningServiceStatus.FINISHED)
                .build();
    }
    
    @Test
    void whenFindAllByUserIdButNoKostRentShouldReturnEmptyList() {
        Integer userId = 0;
        when(kostRentRepository.findAll()).thenReturn(Collections.emptyList());
        when(roomServiceRepository.findAll()).thenReturn(List.of(roomService1, roomService2));

        List<Rental> result = rentalService.findAllByUserId(userId);

        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void whenFindAllByUserIdButUserNeverRentShouldReturnEmptyList() {
        Integer userId = 1;
        when(kostRentRepository.findAll()).thenReturn(List.of(kostRent1, kostRent2, kostRent3));
        when(roomServiceRepository.findAll()).thenReturn(List.of(roomService1, roomService2));

        List<Rental> result = rentalService.findAllByUserId(userId);

        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void whenFindAllByUserIdButUserAlreadyCheckoutAllShouldReturnEmptyList() {
        Integer userId = 0;
        when(kostRentRepository.findAll()).thenReturn(List.of(kostRent1));
        when(roomServiceRepository.findAll()).thenReturn(List.of(roomService1, roomService2));

        List<Rental> result = rentalService.findAllByUserId(userId);

        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void whenFindAllByUserIdAndUserHaventCheckoutAllButNoRoomServiceShouldReturnListOfActiveRentals() {
        Integer userId = 0;
        when(kostRentRepository.findAll()).thenReturn(List.of(kostRent1, kostRent2, kostRent3));
        when(roomServiceRepository.findAll()).thenReturn(Collections.emptyList());

        List<Rental> result = rentalService.findAllByUserId(userId);

        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(2, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(kostRent2, result.get(0).getKostRent());
        assertEquals(Collections.emptyList(), result.get(0).getRoomServices());

        assertEquals(1, result.get(1).getId());
        assertEquals(kostRent3, result.get(1).getKostRent());
        assertEquals(Collections.emptyList(), result.get(1).getRoomServices());
    }

    @Test
    void whenFindAllByUserIdAndUserHaventCheckoutAllAndRoomServiceExistShouldReturnListOfActiveRentals() {
        Integer userId = 0;
        when(kostRentRepository.findAll()).thenReturn(List.of(kostRent1, kostRent2, kostRent3));
        when(roomServiceRepository.findAll()).thenReturn(List.of(roomService1, roomService2));

        List<Rental> result = rentalService.findAllByUserId(userId);

        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(2, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(kostRent2, result.get(0).getKostRent());
        assertEquals(List.of(roomService2), result.get(0).getRoomServices());

        assertEquals(1, result.get(1).getId());
        assertEquals(kostRent3, result.get(1).getKostRent());
        assertEquals(Collections.emptyList(), result.get(1).getRoomServices());
    }

    @Test
    void whenFindAllByUserIdButKostRentExistWithNullShouldReturnListOfActiveRentals() {
        Integer userId = 0;
        when(kostRentRepository.findAll()).thenReturn(Arrays.asList(null, kostRent1, null, kostRent2, kostRent3, null));
        when(roomServiceRepository.findAll()).thenReturn(List.of(roomService1, roomService2));

        List<Rental> result = rentalService.findAllByUserId(userId);

        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(2, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(kostRent2, result.get(0).getKostRent());
        assertEquals(List.of(roomService2), result.get(0).getRoomServices());

        assertEquals(1, result.get(1).getId());
        assertEquals(kostRent3, result.get(1).getKostRent());
        assertEquals(Collections.emptyList(), result.get(1).getRoomServices());
    }

    @Test
    void whenFindAllByUserIdButRoomServiceExistWithNullShouldReturnListOfActiveRentals() {
        Integer userId = 0;
        when(kostRentRepository.findAll()).thenReturn(List.of(kostRent1, kostRent2, kostRent3));
        when(roomServiceRepository.findAll()).thenReturn(Arrays.asList(null, roomService1, null, roomService2, null));

        List<Rental> result = rentalService.findAllByUserId(userId);

        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(2, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(kostRent2, result.get(0).getKostRent());
        assertEquals(List.of(roomService2), result.get(0).getRoomServices());

        assertEquals(1, result.get(1).getId());
        assertEquals(kostRent3, result.get(1).getKostRent());
        assertEquals(Collections.emptyList(), result.get(1).getRoomServices());
    }

    @Test
    void whenGetDataFromFutureInterruptedExceptionShouldThrowAssociatedRentalFutureException() throws NoSuchMethodException, IllegalAccessException {
        CompletableFuture<List<KostRent>> activeRentsFuture = new CompletableFuture<>();
        activeRentsFuture.completeExceptionally(new InterruptedException());

        Method getDataFromFuture = RentalServiceImpl.class.getDeclaredMethod("getDataFromFuture", CompletableFuture.class);
        getDataFromFuture.setAccessible(true);

        try {
            getDataFromFuture.invoke(rentalService, activeRentsFuture);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            assertTrue(cause instanceof RentalFutureException);
            RentalFutureException exception = (RentalFutureException) cause;
            assertEquals("Rental asynchronous computation was interrupted while waiting for the result", exception.getMessage());

        }
    }

    @Test
    void whenGetDataFromFutureExecutionExceptionShouldThrowAssociatedRentalFutureException() throws NoSuchMethodException, IllegalAccessException {
        CompletableFuture<List<KostRent>> activeRentsFuture = new CompletableFuture<>();
        activeRentsFuture.completeExceptionally(new ExecutionException(new RuntimeException("Some error")));

        Method getDataFromFuture = RentalServiceImpl.class.getDeclaredMethod("getDataFromFuture", CompletableFuture.class);
        getDataFromFuture.setAccessible(true);

        try {
            getDataFromFuture.invoke(rentalService, activeRentsFuture);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            assertTrue(cause instanceof RentalFutureException);
            RentalFutureException exception = (RentalFutureException) cause;
            assertEquals("Rental asynchronous computation was executed with error: " +
                            "java.lang.RuntimeException: Some error",
                    exception.getMessage());
        }
    }
}