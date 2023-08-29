package id.ac.ui.cs.advprog.kost.rent.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KostRentRequest {

    private Integer userId;

    private String userName;

    @NotNull
    @Min(1)
    private Integer kostRoomId;

    @NotNull
    private String checkInDate;

    @NotNull
    private String checkOutDate;

    @NotNull
    @Min(1)
    private Integer duration;

    @NotNull
    @DecimalMin("0.1")
    private Double totalPrice;
}
