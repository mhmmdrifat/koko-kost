package id.ac.ui.cs.advprog.kost.rent.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class KostRent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer userId; // yg booking
    private String userName; // yg booking

    private Integer roomNumber;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "kost_room_id", referencedColumnName = "id")
    private KostRoom kostRoom;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date checkInDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date checkOutDate;
    private Integer duration; // in months

    private Double totalPrice;

    public boolean getHasCheckout() {
        return new Date().after(checkOutDate);
    }
}
