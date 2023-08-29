package id.ac.ui.cs.advprog.kost.occupancy.dto;

import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOrder;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;
import java.util.List;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

public class Tenant{
    private Integer id;
    private String tenantName;
    private KostRent kostRent;
    private List<CleaningServiceOrder> roomServices;
    
    public static List<Tenant> createTenants(List<KostRent> activeRents, List<CleaningServiceOrder> roomServices) {
        final var id = new int[]{0};

        return activeRents.stream().map(rent -> {
            List<CleaningServiceOrder> rentServices = roomServices.stream()
                    .filter(service -> service.getKostRent().getId().equals(rent.getId()))
                    .toList();
            return Tenant.builder()
                    .id(id[0]++)
                    .tenantName(rent.getUserName())
                    .kostRent(rent)
                    .roomServices(rentServices)
                    .build();
        }).toList();
    }
}