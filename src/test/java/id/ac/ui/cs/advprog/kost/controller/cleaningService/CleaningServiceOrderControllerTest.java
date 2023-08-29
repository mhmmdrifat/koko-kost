package id.ac.ui.cs.advprog.kost.controller.cleaningService;

import id.ac.ui.cs.advprog.kost.cleaning_service.controller.CleaningServiceOrderController;
import id.ac.ui.cs.advprog.kost.cleaning_service.dto.CleaningServiceMonitoringDTO;
import id.ac.ui.cs.advprog.kost.cleaning_service.dto.CleaningServiceOrderDTO;
import id.ac.ui.cs.advprog.kost.cleaning_service.exception.BookingDurationExceededException;
import id.ac.ui.cs.advprog.kost.cleaning_service.exception.CleaningServiceOrderNotFoundException;
import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOrder;
import id.ac.ui.cs.advprog.kost.cleaning_service.service.CleaningServiceOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CleaningServiceOrderControllerTest {
    private CleaningServiceOrderController cleaningServiceOrderController;

    @Mock
    private CleaningServiceOrderService cleaningServiceOrderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cleaningServiceOrderController = new CleaningServiceOrderController(cleaningServiceOrderService);
    }

    @Test
    void testGetAllCleaningServiceOrders() {
        List<CleaningServiceOrder> orders = new ArrayList<>();
        orders.add(new CleaningServiceOrder());
        orders.add(new CleaningServiceOrder());

        when(cleaningServiceOrderService.getAllCleaningServiceOrders()).thenReturn(orders);

        ResponseEntity<List<CleaningServiceOrder>> response = cleaningServiceOrderController
                .getAllCleaningServiceOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetCleaningServiceOrderById() {
        CleaningServiceOrder order = new CleaningServiceOrder();
        int orderId = 1;

        when(cleaningServiceOrderService.getCleaningServiceOrderById(orderId)).thenReturn(Optional.of(order));

        ResponseEntity<CleaningServiceOrder> response = cleaningServiceOrderController
                .getCleaningServiceOrderById(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order, response.getBody());
    }

    @Test
    void testGetCleaningServiceOrderById_NotFound() {
        int orderId = 1;
        String expectedErrorMessage = "Service order with id " + orderId + " does not exist";

        when(cleaningServiceOrderService.getCleaningServiceOrderById(orderId)).thenReturn(Optional.empty());

        CleaningServiceOrderNotFoundException exception = assertThrows(
                CleaningServiceOrderNotFoundException.class,
                () -> cleaningServiceOrderController.getCleaningServiceOrderById(orderId));

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void testCreateCleaningServiceOrder() {
        CleaningServiceOrderDTO orderDTO = new CleaningServiceOrderDTO();
        CleaningServiceOrder createdOrder = new CleaningServiceOrder();

        when(cleaningServiceOrderService.createCleaningServiceOrder(orderDTO)).thenReturn(createdOrder);

        ResponseEntity<CleaningServiceOrder> response = cleaningServiceOrderController
                .createCleaningServiceOrder(orderDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdOrder, response.getBody());
    }

    @Test
    void testUpdateCleaningServiceOrder() {
        CleaningServiceMonitoringDTO orderDTO = new CleaningServiceMonitoringDTO();
        CleaningServiceOrder updatedOrder = new CleaningServiceOrder();

        when(cleaningServiceOrderService.updateCleaningServiceOrder(anyInt(), any(CleaningServiceMonitoringDTO.class)))
                .thenReturn(Optional.of(updatedOrder));

        ResponseEntity<Optional<CleaningServiceOrder>> response = cleaningServiceOrderController
                .updateCleaningServiceOrder(1, orderDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedOrder, response.getBody().orElse(null));
    }

    @Test
    void testDeleteCleaningServiceOrder() {
        int orderId = 1;

        ResponseEntity<Void> response = cleaningServiceOrderController.deleteCleaningServiceOrder(orderId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cleaningServiceOrderService, times(1)).deleteCleaningServiceOrder(orderId);
    }

    @Test
    void testDeleteCleaningServiceOrder_NotFound() {
        Integer orderId = 1;

        doThrow(new CleaningServiceOrderNotFoundException(orderId))
                .when(cleaningServiceOrderService).deleteCleaningServiceOrder(orderId);

        assertThrows(CleaningServiceOrderNotFoundException.class,
                () -> cleaningServiceOrderController.deleteCleaningServiceOrder(orderId));
    }

    @Test
    void testCreateCleaningServiceOrder_BookingDurationExceeded_Perbulan() {
        CleaningServiceOrderDTO orderDTO = new CleaningServiceOrderDTO();
        orderDTO.setUserId(1);
        orderDTO.setOption("PERBULAN");
        String expectedErrorMessage = "Booking duration exceeds the maximum allowed duration";

        when(cleaningServiceOrderService.createCleaningServiceOrder(orderDTO))
                .thenThrow(new BookingDurationExceededException(expectedErrorMessage));

        BookingDurationExceededException exception = assertThrows(
                BookingDurationExceededException.class,
                () -> cleaningServiceOrderController.createCleaningServiceOrder(orderDTO));

        assertEquals(expectedErrorMessage, exception.getMessage());
    }
    @Test
    void testUpdateCleaningServiceOrder_NotFound() {
        CleaningServiceMonitoringDTO orderDTO = new CleaningServiceMonitoringDTO();
        orderDTO.setStatus("FINISHED");
        int orderId = 1;
        String expectedErrorMessage = "Service order with id " + orderId + " does not exist";

        when(cleaningServiceOrderService.updateCleaningServiceOrder(eq(orderId), any(CleaningServiceMonitoringDTO.class)))
                .thenReturn(Optional.empty());

        ResponseEntity<Optional<CleaningServiceOrder>> response = cleaningServiceOrderController
                .updateCleaningServiceOrder(orderId, orderDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }


}
