package id.ac.ui.cs.advprog.kost.cleaning_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CleaningServiceOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private CleaningServiceStatus status;

    @Enumerated(EnumType.STRING)
    private CleaningServiceOption option;

    @JoinColumn(name = "user_id")
    private Integer UserId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date checkOutDate;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "kost_rent", referencedColumnName = "id")
    private KostRent kostRent;


}
