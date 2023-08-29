package id.ac.ui.cs.advprog.kost.room.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class KostRoom {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;
    @Enumerated(EnumType.STRING)
    private KostRoomType type;

    private String city;
    private String country;
    private String address;

    private String[] facilities;
    private String[] images;

    private Integer stock;

    private Double price;
    private Integer discount; // kalo sewa lebih lama
    private Integer minDiscountDuration; // in months

    public boolean getIsAvailable() {
        return stock > 0;
    }
}
