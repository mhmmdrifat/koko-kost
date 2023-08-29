package id.ac.ui.cs.advprog.kost.bundle.model;

import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class Bundle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "kost_room_id", referencedColumnName = "id")
    private KostRoom kostRoom;
    private Integer coworkingId;

    private Double bundlePrice;
    private Integer duration; // in months for both, fixed

    public Boolean getIsAvailable() {
        return kostRoom.getIsAvailable();
    }
}
