package id.ac.ui.cs.advprog.kost.order.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import id.ac.ui.cs.advprog.kost.bundle.model.Bundle;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class BundleOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer userId;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "bundle_id", referencedColumnName = "id")
    private Bundle bundle;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date checkInDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date checkOutDate;

    public boolean getHasCheckout() {
        return new Date().after(checkOutDate);
    }

}
