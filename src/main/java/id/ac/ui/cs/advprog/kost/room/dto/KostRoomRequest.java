package id.ac.ui.cs.advprog.kost.room.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KostRoomRequest {
    private String name;
    private String type;
    private String city;
    private String country;
    private String address;

    private String[] facilities;
    private String[] images;

    private Integer stock;
    private Double price;
    private Integer discount;
    private Integer minDiscountDuration;
}
