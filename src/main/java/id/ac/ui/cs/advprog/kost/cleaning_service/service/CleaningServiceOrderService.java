package id.ac.ui.cs.advprog.kost.cleaning_service.service;

import id.ac.ui.cs.advprog.kost.cleaning_service.dto.CleaningServiceMonitoringDTO;
import id.ac.ui.cs.advprog.kost.cleaning_service.dto.CleaningServiceOrderDTO;
import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOrder;

import java.util.List;
import java.util.Optional;

public interface CleaningServiceOrderService {
    List<CleaningServiceOrder> getAllCleaningServiceOrders();
    Optional<CleaningServiceOrder> getCleaningServiceOrderById(Integer id);
    CleaningServiceOrder createCleaningServiceOrder(CleaningServiceOrderDTO cleaningServiceOrderDTO);
    Optional<CleaningServiceOrder> updateCleaningServiceOrder(Integer id, CleaningServiceMonitoringDTO cleaningServiceOrderDTO);
    void deleteCleaningServiceOrder(Integer id);
}
