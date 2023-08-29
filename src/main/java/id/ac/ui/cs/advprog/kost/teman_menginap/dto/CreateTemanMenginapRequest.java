package id.ac.ui.cs.advprog.kost.teman_menginap.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTemanMenginapRequest {
    private Integer userId;
    private String name;
    private String email;
    private String reason;
    private String bookingStatus;
}
