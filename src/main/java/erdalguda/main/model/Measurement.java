package erdalguda.main.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity@Table(name = "fabrics")
@Getter
@Setter
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double chest;
    private Double waist;
    private Double hip;
    private Double shoulder;
    private Double neck;
    private Double leftArm;
    private Double rightArm;
    private Double leftThigh;
    private Double rightThigh;
    private Double leftCalf;
    private Double rightCalf;
    private Double elbowLength;

    @OneToOne
    private Customer customer;
}
