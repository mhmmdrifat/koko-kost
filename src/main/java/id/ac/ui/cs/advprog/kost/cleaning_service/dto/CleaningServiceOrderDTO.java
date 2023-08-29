package id.ac.ui.cs.advprog.kost.cleaning_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CleaningServiceOrderDTO {
    private Date startDate;
    private Integer userId;
    private String option;
    //previously private KostRent kostRent
    private Integer kostRentId;

}