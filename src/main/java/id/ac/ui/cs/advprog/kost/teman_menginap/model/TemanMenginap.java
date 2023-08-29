package id.ac.ui.cs.advprog.kost.teman_menginap.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemanMenginap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private String name;
    private String email;
    private String reason;
    private String bookingStatus;
}
