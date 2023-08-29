package id.ac.ui.cs.advprog.kost.bundle.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BundleRequest {
    private String name;

    private Integer kostRoomId;
    private Integer coworkingId;

    private Double bundlePrice;
    private Integer duration;
}
