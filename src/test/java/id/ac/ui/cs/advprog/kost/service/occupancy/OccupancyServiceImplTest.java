package id.ac.ui.cs.advprog.kost.service.occupancy;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOrder;
import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceStatus;
import id.ac.ui.cs.advprog.kost.cleaning_service.repository.CleaningServiceOrderRepository;
import id.ac.ui.cs.advprog.kost.occupancy.exceptions.OccupancyFilterException;
import id.ac.ui.cs.advprog.kost.occupancy.exceptions.OccupancyFutureException;
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
import id.ac.ui.cs.advprog.kost.occupancy.service.OccupancyServiceImpl;
import id.ac.ui.cs.advprog.kost.rent.repository.KostRentRepository;
import id.ac.ui.cs.advprog.kost.room.repository.KostRoomRepository;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.model.KostRoomType;
import id.ac.ui.cs.advprog.kost.occupancy.dto.Tenant;
import id.ac.ui.cs.advprog.kost.occupancy.dto.WithoutTenant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class OccupancyServiceImplTest {

    @Mock
    private KostRoomRepository kostRoomRepository;
    @Mock
    private KostRentRepository kostRentRepository;
    @Mock
    private CleaningServiceOrderRepository roomServiceRepository;

    @InjectMocks
    private OccupancyServiceImpl occupancyService;

    List<GrantedAuthority> authorities = new ArrayList<>();

    KostRoom kostRoom1;
    KostRoom kostRoom2;
    KostRoom kostRoom3;
    KostRent kostRent1;
    KostRent kostRent2;
    KostRent kostRent3;
    KostRent kostRent4;
    CleaningServiceOrder roomService1;
    CleaningServiceOrder roomService2;
    CleaningServiceOrder roomService3;

    @BeforeEach
    void setUp() {
        authorities.add(new SimpleGrantedAuthority("PENGELOLA"));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                null,
                new JwtPayload(5, "PENGELOLA", "Mark", true, (double) 2_000_000),
                authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        kostRoom1 = KostRoom.builder()
                .id(0)
                .name("Campingski Resort")
                .type(KostRoomType.CAMPUR)
                .city("Jakarta")
                .country("Indonesia")
                .address("Jl Kebenaran")
                .facilities(new String[] { "Kamar mandi", "AC" })
                .images(new String[] {
                        "https://res.cloudinary.com/dkg0oswii/image/upload/v1669102647/cld-sample-4.jpg" })
                .stock(3)
                .price((double) 1200000)
                .discount(12)
                .minDiscountDuration(12)
                .build();

        kostRoom2 = KostRoom.builder()
                .id(1)
                .name("Superski Resort")
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

        kostRoom3 = KostRoom.builder()
                .id(2)
                .name("Snowyski Resort")
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

        kostRent1 = KostRent.builder()
                .id(0)
                .userId(0)
                .userName("Agun")
                .roomNumber(3)
                .kostRoom(kostRoom1)
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
                .kostRoom(kostRoom1)
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
                .kostRoom(kostRoom1)
                .checkInDate(Util.parseDate("2023-04-10"))
                .checkOutDate(Util.parseDate("2023-06-10"))
                .duration(3)
                .totalPrice((double) 3600000)
                .build();

        kostRent4 = KostRent.builder()
                .id(3)
                .userId(0)
                .userName("Agun")
                .roomNumber(6)
                .kostRoom(kostRoom2)
                .checkInDate(Util.parseDate("2023-04-10"))
                .checkOutDate(Util.parseDate("2023-06-10"))
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

        roomService3 = CleaningServiceOrder.builder()
                .id(2)
                .UserId(0)
                .kostRent(kostRent2)
                .startDate(Util.parseDate("2023-05-08"))
                .endDate(Util.parseDate("2023-05-08"))
                .status(CleaningServiceStatus.FINISHED)
                .build();
    }

    @Test
    void whenFindAllTenantButNoKostRentShouldReturnEmptyList() {
        when(kostRentRepository.findAll()).thenReturn(Collections.emptyList());
        when(roomServiceRepository.findAll()).thenReturn(List.of(roomService1, roomService2));

        List<Tenant> result = occupancyService.findAllTenant();

        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void whenFindAllTenantAndHadCheckoutAllShouldReturnEmptyList() {
        when(kostRentRepository.findAll()).thenReturn(List.of(kostRent1));
        when(roomServiceRepository.findAll()).thenReturn(List.of(roomService1, roomService2));

        List<Tenant> result = occupancyService.findAllTenant();

        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void whenFindAllTenantAndNoCheckoutButNoRoomServiceShouldReturnListOfActiveTenants() {
        when(kostRentRepository.findAll()).thenReturn(List.of(kostRent2, kostRent3));
        when(roomServiceRepository.findAll()).thenReturn(Collections.emptyList());

        List<Tenant> result = occupancyService.findAllTenant();

        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(2, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(kostRent2.getUserName(), result.get(0).getTenantName());
        assertEquals(kostRent2, result.get(0).getKostRent());
        assertEquals(Collections.emptyList(), result.get(0).getRoomServices());

        assertEquals(1, result.get(1).getId());
        assertEquals(kostRent3.getUserName(), result.get(1).getTenantName());
        assertEquals(kostRent3, result.get(1).getKostRent());
        assertEquals(Collections.emptyList(), result.get(1).getRoomServices());
    }

    @Test
    void whenFindAllTenantAndHaventCheckoutAllButNoRoomServiceShouldReturnListOfActiveTenants() {
        when(kostRentRepository.findAll()).thenReturn(List.of(kostRent1, kostRent2, kostRent3));
        when(roomServiceRepository.findAll()).thenReturn(Collections.emptyList());

        List<Tenant> result = occupancyService.findAllTenant();

        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(2, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(kostRent2.getUserName(), result.get(0).getTenantName());
        assertEquals(kostRent2, result.get(0).getKostRent());
        assertEquals(Collections.emptyList(), result.get(0).getRoomServices());

        assertEquals(1, result.get(1).getId());
        assertEquals(kostRent3.getUserName(), result.get(1).getTenantName());
        assertEquals(kostRent3, result.get(1).getKostRent());
        assertEquals(Collections.emptyList(), result.get(1).getRoomServices());
    }

    @Test
    void whenFindAllTenantAndHaventCheckoutAllAndRoomServiceExistShouldReturnListOfActiveTenants() {
        when(kostRentRepository.findAll()).thenReturn(List.of(kostRent1, kostRent2, kostRent3));
        when(roomServiceRepository.findAll()).thenReturn(List.of(roomService1, roomService2, roomService3));

        List<Tenant> result = occupancyService.findAllTenant();

        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(2, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(kostRent2.getUserName(), result.get(0).getTenantName());
        assertEquals(kostRent2, result.get(0).getKostRent());
        assertEquals(List.of(roomService2, roomService3), result.get(0).getRoomServices());

        assertEquals(1, result.get(1).getId());
        assertEquals(kostRent3.getUserName(), result.get(1).getTenantName());
        assertEquals(kostRent3, result.get(1).getKostRent());
        assertEquals(Collections.emptyList(), result.get(1).getRoomServices());
    }

    @Test
    void whenFindAllTenantButKostRentExistWithNullShouldReturnListOfActiveTenants() {
        when(kostRentRepository.findAll()).thenReturn(Arrays.asList(null, kostRent2, null, kostRent3, null));
        when(roomServiceRepository.findAll()).thenReturn(List.of(roomService1, roomService2, roomService3));

        List<Tenant> result = occupancyService.findAllTenant();

        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(2, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(kostRent2.getUserName(), result.get(0).getTenantName());
        assertEquals(kostRent2, result.get(0).getKostRent());
        assertEquals(List.of(roomService2, roomService3), result.get(0).getRoomServices());

        assertEquals(1, result.get(1).getId());
        assertEquals(kostRent3.getUserName(), result.get(1).getTenantName());
        assertEquals(kostRent3, result.get(1).getKostRent());
        assertEquals(Collections.emptyList(), result.get(1).getRoomServices());
    }

    @Test
    void whenFindAllTenantButRoomServiceExistWithNullShouldReturnListOfActiveTenants() {
        when(kostRentRepository.findAll()).thenReturn(List.of(kostRent2, kostRent3));
        when(roomServiceRepository.findAll()).thenReturn(Arrays.asList(null, roomService1, null, roomService2, roomService3, null));

        List<Tenant> result = occupancyService.findAllTenant();

        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(2, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(kostRent2.getUserName(), result.get(0).getTenantName());
        assertEquals(kostRent2, result.get(0).getKostRent());
        assertEquals(List.of(roomService2, roomService3), result.get(0).getRoomServices());

        assertEquals(1, result.get(1).getId());
        assertEquals(kostRent3.getUserName(), result.get(1).getTenantName());
        assertEquals(kostRent3, result.get(1).getKostRent());
        assertEquals(Collections.emptyList(), result.get(1).getRoomServices());
    }

    @Test
    void whenFindAllTenantByRoomNameAndNotFoundShouldThrowException() {
        when(kostRoomRepository.findByNameIgnoreCase(any(String.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(OccupancyFilterException.class, () -> {
            occupancyService.findAllTenantByRoomName("test");
        });
    }

    @Test
    void whenFindAllTenantByRoomNameAndFoundButHadCheckoutAllShouldReturnEmptyList() {
        when(kostRoomRepository.findByNameIgnoreCase(any(String.class))).thenReturn(Optional.of(kostRoom3));
        when(kostRentRepository.findAll()).thenReturn(List.of(kostRent1, kostRent2, kostRent3, kostRent4));
        when(roomServiceRepository.findAll()).thenReturn(List.of(roomService1, roomService2, roomService3));

        List<Tenant> result = occupancyService.findAllTenantByRoomName(kostRoom3.getName());

        verify(kostRoomRepository, atLeastOnce()).findByNameIgnoreCase(any(String.class));
        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void whenFindAllTenantByRoomNameAndFoundAndHaventCheckoutAllShouldReturnListOfActiveTenants() {
        when(kostRoomRepository.findByNameIgnoreCase(any(String.class))).thenReturn(Optional.of(kostRoom1));
        when(kostRentRepository.findAll()).thenReturn(List.of(kostRent1, kostRent2, kostRent3, kostRent4));
        when(roomServiceRepository.findAll()).thenReturn(List.of(roomService1, roomService2, roomService3));

        List<Tenant> result = occupancyService.findAllTenantByRoomName(kostRoom1.getName());

        verify(kostRoomRepository, atLeastOnce()).findByNameIgnoreCase(any(String.class));
        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(2, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(kostRent2.getUserName(), result.get(0).getTenantName());
        assertEquals(kostRent2, result.get(0).getKostRent());
        assertEquals(List.of(roomService2, roomService3), result.get(0).getRoomServices());

        assertEquals(1, result.get(1).getId());
        assertEquals(kostRent3.getUserName(), result.get(1).getTenantName());
        assertEquals(kostRent3, result.get(1).getKostRent());
        assertEquals(Collections.emptyList(), result.get(1).getRoomServices());
    }

    @Test
    void whenFindAllTenantByRoomNameButKostRentExistWithNullShouldReturnListOfActiveTenants() {
        when(kostRoomRepository.findByNameIgnoreCase(any(String.class))).thenReturn(Optional.of(kostRoom1));
        when(kostRentRepository.findAll()).thenReturn(Arrays.asList(null, kostRent2, null, kostRent3, null));
        when(roomServiceRepository.findAll()).thenReturn(List.of(roomService1, roomService2, roomService3));

        List<Tenant> result = occupancyService.findAllTenantByRoomName(kostRoom1.getName());

        verify(kostRoomRepository, atLeastOnce()).findByNameIgnoreCase(any(String.class));
        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(2, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(kostRent2.getUserName(), result.get(0).getTenantName());
        assertEquals(kostRent2, result.get(0).getKostRent());
        assertEquals(List.of(roomService2, roomService3), result.get(0).getRoomServices());

        assertEquals(1, result.get(1).getId());
        assertEquals(kostRent3.getUserName(), result.get(1).getTenantName());
        assertEquals(kostRent3, result.get(1).getKostRent());
        assertEquals(Collections.emptyList(), result.get(1).getRoomServices());
    }

    @Test
    void whenFindAllTenantByRoomNameButRoomServiceExistWithNullShouldReturnListOfActiveTenants() {
        when(kostRoomRepository.findByNameIgnoreCase(any(String.class))).thenReturn(Optional.of(kostRoom1));
        when(kostRentRepository.findAll()).thenReturn(List.of(kostRent2, kostRent3));
        when(roomServiceRepository.findAll()).thenReturn(Arrays.asList(null, roomService1, null, roomService2, roomService3, null));

        List<Tenant> result = occupancyService.findAllTenantByRoomName(kostRoom1.getName());

        verify(kostRoomRepository, atLeastOnce()).findByNameIgnoreCase(any(String.class));
        verify(kostRentRepository, atLeastOnce()).findAll();
        verify(roomServiceRepository, atLeastOnce()).findAll();

        assertEquals(2, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(kostRent2.getUserName(), result.get(0).getTenantName());
        assertEquals(kostRent2, result.get(0).getKostRent());
        assertEquals(List.of(roomService2, roomService3), result.get(0).getRoomServices());

        assertEquals(1, result.get(1).getId());
        assertEquals(kostRent3.getUserName(), result.get(1).getTenantName());
        assertEquals(kostRent3, result.get(1).getKostRent());
        assertEquals(Collections.emptyList(), result.get(1).getRoomServices());
    }

    @Test
    void whenFindAllWithoutTenantButNoKostRoomShouldReturnEmptyList() {
        when(kostRoomRepository.findAll()).thenReturn(Collections.emptyList());

        List<WithoutTenant> result = occupancyService.findAllWithoutTenant();

        verify(kostRoomRepository, atLeastOnce()).findAll();

        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void whenFindAllWithoutTenantAndOneKostRoomFoundShouldReturnCorrectNumberOfObjects() {
        when(kostRoomRepository.findAll()).thenReturn(List.of(kostRoom1));

        List<WithoutTenant> result = occupancyService.findAllWithoutTenant();

        verify(kostRoomRepository, atLeastOnce()).findAll();

        assertEquals(3, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(1, result.get(1).getId());
        assertEquals(2, result.get(2).getId());

        assertEquals(kostRoom1, result.get(0).getKostRoom());
        assertEquals(kostRoom1, result.get(1).getKostRoom());
        assertEquals(kostRoom1, result.get(2).getKostRoom());
    }

    @Test
    void whenFindAllWithoutTenantAndMultipleKostRoomFoundShouldReturnCorrectNumberOfObjects() {
        when(kostRoomRepository.findAll()).thenReturn(List.of(kostRoom1, kostRoom2));

        List<WithoutTenant> result = occupancyService.findAllWithoutTenant();

        verify(kostRoomRepository, atLeastOnce()).findAll();

        assertEquals(5, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(1, result.get(1).getId());
        assertEquals(2, result.get(2).getId());
        assertEquals(3, result.get(3).getId());
        assertEquals(4, result.get(4).getId());

        assertEquals(kostRoom1, result.get(0).getKostRoom());
        assertEquals(kostRoom1, result.get(1).getKostRoom());
        assertEquals(kostRoom1, result.get(2).getKostRoom());
        assertEquals(kostRoom2, result.get(3).getKostRoom());
        assertEquals(kostRoom2, result.get(4).getKostRoom());
    }
    
    @Test
    void whenFindAllWithoutTenantByRoomNameAndNotFoundShouldThrowException() {
        when(kostRoomRepository.findByNameIgnoreCase(any(String.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(OccupancyFilterException.class, () -> {
            occupancyService.findAllWithoutTenantByRoomName("test");
        });
    }

    @Test
    void whenFindAllWithoutTenantByRoomNameAndFoundShouldReturnCorrectNumberOfObjects() {
        when(kostRoomRepository.findByNameIgnoreCase(any(String.class))).thenReturn(Optional.of(kostRoom1));

        List<WithoutTenant> result = occupancyService.findAllWithoutTenantByRoomName(kostRoom1.getName());

        verify(kostRoomRepository, atLeastOnce()).findByNameIgnoreCase(any(String.class));

        assertEquals(3, result.size());

        assertEquals(0, result.get(0).getId());
        assertEquals(1, result.get(1).getId());
        assertEquals(2, result.get(2).getId());

        assertEquals(kostRoom1, result.get(0).getKostRoom());
        assertEquals(kostRoom1, result.get(1).getKostRoom());
        assertEquals(kostRoom1, result.get(2).getKostRoom());
    }

    @Test
    void whenGetDataFromFutureInterruptedExceptionShouldThrowAssociatedOccupancyFutureException() throws NoSuchMethodException, IllegalAccessException {
        CompletableFuture<List<KostRent>> activeRentsFuture = new CompletableFuture<>();
        activeRentsFuture.completeExceptionally(new InterruptedException());

        Method getDataFromFuture = OccupancyServiceImpl.class.getDeclaredMethod("getDataFromFuture", CompletableFuture.class);
        getDataFromFuture.setAccessible(true);

        try {
            getDataFromFuture.invoke(occupancyService, activeRentsFuture);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            assertTrue(cause instanceof OccupancyFutureException);
            OccupancyFutureException exception = (OccupancyFutureException) cause;
            assertEquals("Tenant asynchronous computation was interrupted while waiting for the result", exception.getMessage());

        }
    }

    @Test
    void whenGetDataFromFutureExecutionExceptionShouldThrowAssociatedOccupancyFutureException() throws NoSuchMethodException, IllegalAccessException {
        CompletableFuture<List<KostRent>> activeRentsFuture = new CompletableFuture<>();
        activeRentsFuture.completeExceptionally(new ExecutionException(new RuntimeException("Some error")));

        Method getDataFromFuture = OccupancyServiceImpl.class.getDeclaredMethod("getDataFromFuture", CompletableFuture.class);
        getDataFromFuture.setAccessible(true);

        try {
            getDataFromFuture.invoke(occupancyService, activeRentsFuture);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            assertTrue(cause instanceof OccupancyFutureException);
            OccupancyFutureException exception = (OccupancyFutureException) cause;
            assertEquals("Tenant asynchronous computation was executed with error: " +
                            "java.lang.RuntimeException: Some error",
                    exception.getMessage());
        }
    }
}