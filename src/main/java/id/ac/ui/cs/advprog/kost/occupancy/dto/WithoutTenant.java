package id.ac.ui.cs.advprog.kost.occupancy.dto;

import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import java.util.Collections;
import java.util.List;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

public class WithoutTenant {
    private Integer id;
    private Integer roomNumber;
    private KostRoom kostRoom;

    public static List<WithoutTenant> createWithoutTenants(KostRoom kostRoom, int[] id) {
        final var counter = new int[]{0};

        return Collections.nCopies(kostRoom.getStock(), kostRoom)
                .stream()
                .map(room -> WithoutTenant.builder()
                        .id(id[0]++)
                        .roomNumber(++counter[0])
                        .kostRoom(room)
                        .build())
                .toList();
    }
}
