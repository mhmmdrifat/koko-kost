package id.ac.ui.cs.advprog.kost.order.dto;

import java.util.Date;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BundleOrderRequest {

    private Integer bundleId;

    private Integer userId;

    private Date checkInDate;

    private Date checkOutDate;

}
