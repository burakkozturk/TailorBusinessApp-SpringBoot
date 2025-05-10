package erdalguda.main.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String address;
    private String phone;

    private Double height; // cm
    private Double weight; // kg

    private String ocrMeasurementText; // OCR sonucu düz metin olarak tutulur
}
