package id.ac.ui.cs.advprog.kost.cleaning_service.controller;

import id.ac.ui.cs.advprog.kost.cleaning_service.dto.CleaningServiceMonitoringDTO;
import id.ac.ui.cs.advprog.kost.cleaning_service.dto.CleaningServiceOrderDTO;
import id.ac.ui.cs.advprog.kost.cleaning_service.exception.BookingDurationExceededException;
import id.ac.ui.cs.advprog.kost.cleaning_service.exception.CleaningServiceOrderNotFoundException;
import id.ac.ui.cs.advprog.kost.cleaning_service.exception.KostRentNotFoundException;
import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOrder;
import id.ac.ui.cs.advprog.kost.cleaning_service.service.CleaningServiceOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cleaningService-orders")
public class CleaningServiceOrderController {
    private final CleaningServiceOrderService cleaningServiceOrderService;

    @Autowired
    public CleaningServiceOrderController(CleaningServiceOrderService cleaningServiceOrderService) {
        this.cleaningServiceOrderService = cleaningServiceOrderService;
    }

    @GetMapping("/")
    public ResponseEntity<List<CleaningServiceOrder>> getAllCleaningServiceOrders() {
        List<CleaningServiceOrder> cleaningServiceOrders = cleaningServiceOrderService.getAllCleaningServiceOrders();
        return ResponseEntity.ok(cleaningServiceOrders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CleaningServiceOrder> getCleaningServiceOrderById(@PathVariable("id") Integer id) {
        CleaningServiceOrder cleaningServiceOrder = cleaningServiceOrderService.getCleaningServiceOrderById(id)
                .orElseThrow(() -> new CleaningServiceOrderNotFoundException(id));
        return ResponseEntity.ok(cleaningServiceOrder);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PELANGGAN')")
    public ResponseEntity<CleaningServiceOrder> createCleaningServiceOrder(
            @RequestBody CleaningServiceOrderDTO cleaningServiceOrderDTO) {
        CleaningServiceOrder newOrder = cleaningServiceOrderService.createCleaningServiceOrder(cleaningServiceOrderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<Optional<CleaningServiceOrder>> updateCleaningServiceOrder(
            @PathVariable("id") Integer id, @RequestBody CleaningServiceMonitoringDTO cleaningServiceMonitoringDTO) {
        Optional<CleaningServiceOrder> updatedCleaningServiceOrder = cleaningServiceOrderService
                .updateCleaningServiceOrder(id, cleaningServiceMonitoringDTO);

        if (updatedCleaningServiceOrder.isPresent()) {
            return ResponseEntity.ok(updatedCleaningServiceOrder);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasAuthority('PENGELOLA')")
    public ResponseEntity<Void> deleteCleaningServiceOrder(@PathVariable("id") Integer id) {
        cleaningServiceOrderService.deleteCleaningServiceOrder(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CleaningServiceOrderNotFoundException.class)
    public ResponseEntity<String> handleCleaningServiceOrderNotFoundException(
            CleaningServiceOrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(BookingDurationExceededException.class)
    public ResponseEntity<String> handleBookingDurationExceededException(BookingDurationExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");

    }

    @ExceptionHandler(KostRentNotFoundException.class)
    public ResponseEntity<String> handleKostRentNotFoundException(KostRentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
