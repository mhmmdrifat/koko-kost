package id.ac.ui.cs.advprog.kost.service.cleaningService;

import id.ac.ui.cs.advprog.kost.cleaning_service.dto.CleaningServiceMonitoringDTO;
import id.ac.ui.cs.advprog.kost.cleaning_service.dto.CleaningServiceOrderDTO;
import id.ac.ui.cs.advprog.kost.cleaning_service.exception.BookingDurationExceededException;
import id.ac.ui.cs.advprog.kost.cleaning_service.exception.CleaningServiceOrderNotFoundException;
import id.ac.ui.cs.advprog.kost.cleaning_service.exception.KostRentNotFoundException;
import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOption;
import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOrder;
import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceStatus;
import id.ac.ui.cs.advprog.kost.cleaning_service.repository.CleaningServiceOrderRepository;
import id.ac.ui.cs.advprog.kost.cleaning_service.service.CleaningServiceOrderService;
import id.ac.ui.cs.advprog.kost.cleaning_service.service.CleaningServiceOrderServiceImpl;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;
import id.ac.ui.cs.advprog.kost.rent.repository.KostRentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CleaningServiceOrderServiceImplTest {

    @Mock
    private CleaningServiceOrderRepository cleaningServiceOrderRepository;

    @Mock
    private KostRentRepository kostRentRepository;

    @InjectMocks
    private CleaningServiceOrderServiceImpl cleaningServiceOrderService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        kostRentRepository = Mockito.mock(KostRentRepository.class);
        cleaningServiceOrderService = new CleaningServiceOrderServiceImpl(cleaningServiceOrderRepository, kostRentRepository);
    }

    @Test
    void testGetAllCleaningServiceOrders() {
        // Prepare test data
        CleaningServiceOrder order1 = new CleaningServiceOrder();
        CleaningServiceOrder order2 = new CleaningServiceOrder();
        List<CleaningServiceOrder> expectedOrders = new ArrayList<>();
        expectedOrders.add(order1);
        expectedOrders.add(order2);

        // Mock the repository method
        when(cleaningServiceOrderRepository.findAll()).thenReturn(expectedOrders);

        // Call the service method
        List<CleaningServiceOrder> result = cleaningServiceOrderService.getAllCleaningServiceOrders();

        // Verify the result
        assertEquals(expectedOrders, result);
        verify(cleaningServiceOrderRepository, times(1)).findAll();
    }

    @Test
    void testCreateCleaningServiceOrderWithPerbulanOption() {
        // Prepare test data
        CleaningServiceOrderDTO cleaningServiceOrderDTO = new CleaningServiceOrderDTO();
        cleaningServiceOrderDTO.setUserId(1);
        cleaningServiceOrderDTO.setOption(CleaningServiceOption.Perbulan.toString());
        cleaningServiceOrderDTO.setStartDate(new Date());

        // Mock the repository methods
        when(kostRentRepository.findAll()).thenReturn(Collections.emptyList());

        // Call the service method and catch the exception
        Exception exception = assertThrows(KostRentNotFoundException.class, () ->
                cleaningServiceOrderService.createCleaningServiceOrder(cleaningServiceOrderDTO));

        // Verify the exception
        assertEquals("KostRent with ID 1 not found.", exception.getMessage());
        verify(kostRentRepository, times(1)).findAll();
    }

    @Test
    void testGetCleaningServiceOrderById() {
        // Prepare test data
        CleaningServiceOrder expectedOrder = new CleaningServiceOrder();
        expectedOrder.setId(1);

        // Mock the repository method
        when(cleaningServiceOrderRepository.findById(1)).thenReturn(Optional.of(expectedOrder));

        // Call the service method
        Optional<CleaningServiceOrder> result = cleaningServiceOrderService.getCleaningServiceOrderById(1);

        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(expectedOrder, result.get());
        verify(cleaningServiceOrderRepository, times(1)).findById(1);
    }

    @Test
    void testGetCleaningServiceOrderByIdNotFound() {
        // Mock the repository method
        when(cleaningServiceOrderRepository.findById(1)).thenReturn(Optional.empty());

        // Call the service method and verify the exception
        assertThrows(CleaningServiceOrderNotFoundException.class, () -> cleaningServiceOrderService.getCleaningServiceOrderById(1));
        verify(cleaningServiceOrderRepository, times(1)).findById(1);
    }


    @Test
    void testUpdateCleaningServiceOrder() {
        // Prepare test data
        Integer orderId = 1;
        CleaningServiceMonitoringDTO monitoringDTO = new CleaningServiceMonitoringDTO();
        monitoringDTO.setStatus(CleaningServiceStatus.FINISHED.toString());

        CleaningServiceOrder existingOrder = new CleaningServiceOrder();
        existingOrder.setId(orderId);
        existingOrder.setStatus(CleaningServiceStatus.PENDING);

        CleaningServiceOrder updatedOrder = new CleaningServiceOrder();
        updatedOrder.setId(orderId);
        updatedOrder.setStatus(CleaningServiceStatus.FINISHED);

        // Mock the repository methods
        when(cleaningServiceOrderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(cleaningServiceOrderRepository.save(existingOrder)).thenReturn(updatedOrder);

        // Call the service method
        Optional<CleaningServiceOrder> result = cleaningServiceOrderService.updateCleaningServiceOrder(orderId, monitoringDTO);

        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(updatedOrder, result.get());
        verify(cleaningServiceOrderRepository, times(1)).findById(orderId);
        verify(cleaningServiceOrderRepository, times(1)).save(existingOrder);
    }

    @Test
    void testUpdateCleaningServiceOrderNotFound() {
        // Prepare test data
        Integer orderId = 1;
        CleaningServiceMonitoringDTO monitoringDTO = new CleaningServiceMonitoringDTO();
        monitoringDTO.setStatus(CleaningServiceStatus.FINISHED.toString());

        // Mock the repository method
        when(cleaningServiceOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Call the service method and verify the exception
        assertThrows(CleaningServiceOrderNotFoundException.class, () ->
                cleaningServiceOrderService.updateCleaningServiceOrder(orderId, monitoringDTO));
        verify(cleaningServiceOrderRepository, times(1)).findById(orderId);
        verify(cleaningServiceOrderRepository, never()).save(any());
    }

    @Test
    void testDeleteCleaningServiceOrder() {
        // Prepare test data
        Integer orderId = 1;

        // Call the service method
        cleaningServiceOrderService.deleteCleaningServiceOrder(orderId);

        // Verify the repository method was called
        verify(cleaningServiceOrderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void testGetKostRentByUserId() {
        // Prepare test data
        Integer userId = 1;
        KostRent expectedKostRent = new KostRent();
        expectedKostRent.setUserId(userId);

        // Mock the repository method
        when(kostRentRepository.findAll()).thenReturn(Collections.singletonList(expectedKostRent));

        // Call the service method
        KostRent result = cleaningServiceOrderService.getKostRentByUserId(userId);

        // Verify the result
        assertNotNull(result);
        assertEquals(expectedKostRent, result);
        verify(kostRentRepository, times(1)).findAll();
    }

    @Test
    void testGetKostRentByUserIdNotFound() {
        // Prepare test data
        Integer userId = 1;

        // Mock the repository method
        when(kostRentRepository.findAll()).thenReturn(Collections.emptyList());

        // Call the service method and verify the result
        KostRent result = cleaningServiceOrderService.getKostRentByUserId(userId);

        assertNull(result);
        verify(kostRentRepository, times(1)).findAll();
    }

    @Test
    void testCreateCleaningServiceOrderWithInvalidUserId() {
        // Prepare test data
        CleaningServiceOrderDTO cleaningServiceOrderDTO = new CleaningServiceOrderDTO();
        cleaningServiceOrderDTO.setUserId(999); // Invalid userId
        cleaningServiceOrderDTO.setOption(CleaningServiceOption.Perhari.toString());
        cleaningServiceOrderDTO.setStartDate(new Date());

        // Mock the repository method
        when(kostRentRepository.findAll()).thenReturn(Collections.emptyList());

        // Call the service method and verify the exception
        assertThrows(KostRentNotFoundException.class, () ->
                cleaningServiceOrderService.createCleaningServiceOrder(cleaningServiceOrderDTO));
        verify(kostRentRepository, times(1)).findAll();
    }

    @Test
    void testCreateCleaningServiceOrderWithInvalidBookingDuration() {
        // Prepare test data
        CleaningServiceOrderDTO cleaningServiceOrderDTO = new CleaningServiceOrderDTO();
        cleaningServiceOrderDTO.setUserId(1);
        cleaningServiceOrderDTO.setOption(CleaningServiceOption.Perhari.toString());
        cleaningServiceOrderDTO.setStartDate(new Date());

        KostRent kostRent = new KostRent();
        kostRent.setUserId(1);
        // Set check-out date to a date that does not allow booking duration
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1); // Add 1 day to the current date
        kostRent.setCheckOutDate(calendar.getTime());

        // Mock the repository methods
        when(kostRentRepository.findAll()).thenReturn(Collections.singletonList(kostRent));

        // Call the service method and verify the exception
        assertThrows(BookingDurationExceededException.class, () ->
                cleaningServiceOrderService.createCleaningServiceOrder(cleaningServiceOrderDTO));
        verify(kostRentRepository, times(1)).findAll();
    }

    @Test
    void testCalculateEndDateForPerhari() {
        // Prepare test data
        Date startDate = new Date();

        // Call the private method
        Date result = invokePrivateMethod(cleaningServiceOrderService, "calculateEndDateForPerhari", startDate);

        // Verify the result
        Calendar expectedEndDate = Calendar.getInstance();
        expectedEndDate.setTime(startDate);
        expectedEndDate.add(Calendar.DAY_OF_MONTH, 1); // Add 1 day to the start date
        assertEquals(expectedEndDate.getTime(), result);
    }


    @Test
    void testGetCheckOutDateByUserId() {
        // Prepare test data
        Integer userId = 1;
        Date expectedCheckOutDate = new Date();

        KostRent kostRent = new KostRent();
        kostRent.setUserId(userId);
        kostRent.setCheckOutDate(expectedCheckOutDate);

        // Mock the repository methods
        when(kostRentRepository.findAll()).thenReturn(Collections.singletonList(kostRent));

        // Call the service method
        Date result = cleaningServiceOrderService.getCheckOutDateByUserId(userId);

        // Verify the result
        assertEquals(expectedCheckOutDate, result);
        verify(kostRentRepository, times(1)).findAll();
    }

    @Test
    void testGetCheckOutDateByUserIdWithInvalidUserId() {
        // Prepare test data
        Integer userId = 1;

        // Mock the repository methods
        when(kostRentRepository.findAll()).thenReturn(Collections.emptyList());

        // Call the service method and expect an exception
        assertThrows(KostRentNotFoundException.class, () -> {
            cleaningServiceOrderService.getCheckOutDateByUserId(userId);
        });

        // Verify the repository method was called
        verify(kostRentRepository, times(1)).findAll();
    }

    // Helper method to invoke private methods
    private <T> T invokePrivateMethod(Object object, String methodName, Object... arguments) {
        try {
            Class<?>[] parameterTypes = new Class[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                parameterTypes[i] = arguments[i].getClass();
            }
            Method method = object.getClass().getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return (T) method.invoke(object, arguments);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke private method: " + methodName, e);
        }
    }

    @Test
    void testCreateCleaningServiceOrderWithInvalidOption() {
        // Prepare test data
        CleaningServiceOrderDTO cleaningServiceOrderDTO = new CleaningServiceOrderDTO();
        cleaningServiceOrderDTO.setUserId(1);
        cleaningServiceOrderDTO.setOption("InvalidOption");
        cleaningServiceOrderDTO.setStartDate(new Date());

        KostRent kostRent = new KostRent();
        kostRent.setUserId(1);

        // Mock the repository methods
        when(kostRentRepository.findAll()).thenReturn(Collections.singletonList(kostRent));

        // Call the service method and verify the exception
        assertThrows(IllegalArgumentException.class, () ->
                cleaningServiceOrderService.createCleaningServiceOrder(cleaningServiceOrderDTO));
        verify(kostRentRepository, times(1)).findAll();
    }


    @Test
    void testGetKostRentByUserIdWithMultipleKostRents() {
        // Prepare test data
        Integer userId = 1;
        KostRent kostRent1 = new KostRent();
        kostRent1.setUserId(userId);
        KostRent kostRent2 = new KostRent();
        kostRent2.setUserId(userId);
        List<KostRent> kostRents = Arrays.asList(kostRent1, kostRent2);

        // Mock the repository method
        when(kostRentRepository.findAll()).thenReturn(kostRents);

        // Call the service method and verify the result
        KostRent result = cleaningServiceOrderService.getKostRentByUserId(userId);
        assertNotNull(result);
        assertEquals(kostRent1, result);
        verify(kostRentRepository, times(1)).findAll();
    }


}


