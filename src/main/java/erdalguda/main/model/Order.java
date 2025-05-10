package erdalguda.main.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productType; // CEKET, GÖMLEK, PANTOLON
    private String fitType;     // SLIM, REGULAR, BAGGY

    private LocalDate orderDate = LocalDate.now();

    private String status = "Hazırlanıyor"; // default
    private Long selectedFabricId; // kumaş ID’si (ileride ilişkilendirilecek)
    private String notes;


    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // İleride PDF/DXF linkleri buraya eklenebilir
}
