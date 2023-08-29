package id.ac.ui.cs.advprog.kost.rental.dto;

import java.util.List;

import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOrder;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

public class Rental {
    private Integer id;
    private KostRent kostRent;
    private List<CleaningServiceOrder> roomServices;

    public static List<Rental> createRentals(List<KostRent> activeRents, List<CleaningServiceOrder> roomServices) {
        final var id = new int[]{0};

        return activeRents.stream().map(rent -> {
            List<CleaningServiceOrder> rentServices = roomServices.stream()
                    .filter(service -> service.getKostRent().getId().equals(rent.getId()))
                    .toList();
            return Rental.builder()
                    .id(id[0]++)
                    .kostRent(rent)
                    .roomServices(rentServices)
                    .build();
        }).toList();
    }
}