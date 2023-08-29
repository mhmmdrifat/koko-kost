package id.ac.ui.cs.advprog.kost.cleaning_service.service;

import id.ac.ui.cs.advprog.kost.cleaning_service.dto.CleaningServiceMonitoringDTO;
import id.ac.ui.cs.advprog.kost.cleaning_service.dto.CleaningServiceOrderDTO;
import id.ac.ui.cs.advprog.kost.cleaning_service.exception.BookingDurationExceededException;
import id.ac.ui.cs.advprog.kost.cleaning_service.exception.CleaningServiceOrderNotFoundException;
import id.ac.ui.cs.advprog.kost.cleaning_service.exception.KostRentNotFoundException;
import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOption;
import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOrder;
import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceStatus;
import id.ac.ui.cs.advprog.kost.cleaning_service.repository.CleaningServiceOrderRepository;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;
import id.ac.ui.cs.advprog.kost.rent.repository.KostRentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CleaningServiceOrderServiceImpl implements CleaningServiceOrderService {
    private final CleaningServiceOrderRepository cleaningServiceOrderRepository;
    private final KostRentRepository kostRentRepository;

    @Autowired
    public CleaningServiceOrderServiceImpl(
            CleaningServiceOrderRepository cleaningServiceOrderRepository, KostRentRepository kostRentRepository) {
        this.cleaningServiceOrderRepository = cleaningServiceOrderRepository;
        this.kostRentRepository = kostRentRepository;
    }

    public List<CleaningServiceOrder> getAllCleaningServiceOrders() {
        return cleaningServiceOrderRepository.findAll();
    }

    public Optional<CleaningServiceOrder> getCleaningServiceOrderById(Integer id) {
        return cleaningServiceOrderRepository.findById(id)
                .map(Optional::of)
                .orElseThrow(() -> new CleaningServiceOrderNotFoundException(id));
    }

    public CleaningServiceOrder createCleaningServiceOrder(CleaningServiceOrderDTO cleaningServiceOrderDTO) {
        validateBookingDuration(getCheckOutDateByUserId(cleaningServiceOrderDTO.getUserId()),
                CleaningServiceOption.valueOf(cleaningServiceOrderDTO.getOption()));
        Date setEndDate = new Date();

        if (CleaningServiceOption.Perbulan.toString().equalsIgnoreCase(cleaningServiceOrderDTO.getOption())) {
            setEndDate = calculateEndDateForPerbulan(cleaningServiceOrderDTO.getStartDate());
        } else if (CleaningServiceOption.Perhari.toString().equalsIgnoreCase(cleaningServiceOrderDTO.getOption())) {
            setEndDate = calculateEndDateForPerhari(cleaningServiceOrderDTO.getStartDate());
        }

        KostRent kostRent = getKostRentByUserId(cleaningServiceOrderDTO.getUserId());

        if (kostRent != null) {
            CleaningServiceOrder newOrder = CleaningServiceOrder.builder()
                    .startDate(cleaningServiceOrderDTO.getStartDate())
                    .endDate(setEndDate)
                    .status(CleaningServiceStatus.PENDING)
                    .UserId(cleaningServiceOrderDTO.getUserId())
                    .checkOutDate(getCheckOutDateByUserId(cleaningServiceOrderDTO.getUserId()))
                    .option(CleaningServiceOption.valueOf(cleaningServiceOrderDTO.getOption()))
                    .kostRent(kostRent)
                    .build();

            return cleaningServiceOrderRepository.save(newOrder);
        }

        return null; // Return null if the KostRent with the specified userId is not found
    }


    public Optional<CleaningServiceOrder> updateCleaningServiceOrder(
            Integer id, CleaningServiceMonitoringDTO cleaningServiceMonitoringDTO) {
        CleaningServiceOrder cleaningServiceOrder = cleaningServiceOrderRepository.findById(id)
                .orElseThrow(() -> new CleaningServiceOrderNotFoundException(id));

        cleaningServiceOrder.setStatus(CleaningServiceStatus.valueOf(cleaningServiceMonitoringDTO.getStatus()));

        return Optional.of(cleaningServiceOrderRepository.save(cleaningServiceOrder));
    }

    public KostRent getKostRentByUserId(Integer userId) {
        List<KostRent> kostRents = kostRentRepository.findAll();

        for (KostRent kostRent : kostRents) {
            if (kostRent.getUserId().equals(userId)) {
                return kostRent;
            }
        }
        return null;
    }

    private boolean validateBookingDuration(Date checkOutDate, CleaningServiceOption option) {
        if (checkOutDate == null) {
            throw new IllegalArgumentException("Check out date is null");
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate checkOutLocalDate = checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (option.equals(CleaningServiceOption.Perhari)) {
            LocalDate startDate = currentDate.plusDays(1);
            if (startDate.isAfter(checkOutLocalDate) || startDate.isEqual(checkOutLocalDate)) {
                throw new BookingDurationExceededException("Booking duration exceeds the maximum allowed duration");
            }
        } else if (option.equals(CleaningServiceOption.Perbulan)) {
            LocalDate startDate = currentDate.plusDays(1);
            LocalDate endDate = startDate.plusMonths(1);

            if (endDate.isBefore(checkOutLocalDate) || endDate.isEqual(checkOutLocalDate)) {
                throw new BookingDurationExceededException("Booking duration exceeds the maximum allowed duration");
            }
        }

        return true;
    }

    //method to calculate the end date for Perbulan option
    private Date calculateEndDateForPerbulan(Date startDate) {
        // Use your own logic to calculate the end date based on the start date and boarding house rental duration
        LocalDate localStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localEndDate = localStartDate.plusMonths(1); // Assuming one month duration
        return Date.from(localEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date calculateEndDateForPerhari(Date startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1); // Add 1 day to the startDate
        return calendar.getTime();
    }

    public Date getCheckOutDateByUserId(Integer userId) {
        List<KostRent> kostRents = kostRentRepository.findAll();

        for (KostRent kostRent : kostRents) {
            if (kostRent.getUserId().equals(userId)) {
                Date checkoutDate = kostRent.getCheckOutDate();
                if (checkoutDate != null) {
                    return checkoutDate;
                } else {
                    throw new IllegalArgumentException("You need to book a kost first.");
                }
            }
        }

        throw new KostRentNotFoundException(userId);
    }

    public void deleteCleaningServiceOrder(Integer id) {
        cleaningServiceOrderRepository.deleteById(id);
    }


}