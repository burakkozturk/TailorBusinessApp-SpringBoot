package erdalguda.main.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    private FitType fitType;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDate orderDate = LocalDate.now();
    private LocalDate estimatedDeliveryDate;
    private LocalDate deliveryDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PREPARING;

    private String notes;

    // İlişkiler
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "fabric_id")
    private Fabric fabric;

    // PDF/DXF dosya bilgileri
    private String patternFilePath;
    private String patternFileType; // PDF, DXF

    // Fiyat bilgileri
    private Double totalPrice;


    // Enum'lar
    public enum ProductType {
        CEKET("Ceket"),
        GÖMLEK("Gömlek"),
        PANTOLON("Pantolon"),
        YELEK("Yelek"),
        TAKIM("Takım Elbise");

        private final String displayName;

        ProductType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum FitType {
        SLIM("Slim Fit"),
        REGULAR("Regular Fit"),
        BAGGY("Baggy Fit"),
        CUSTOM("Özel Kesim");

        private final String displayName;

        FitType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum OrderStatus {
        PREPARING("Hazırlanıyor"),
        CUTTING("Kesim Aşamasında"),
        SEWING("Dikim Aşamasında"),
        FITTING("Prova Aşamasında"),
        READY("Hazır"),
        DELIVERED("Teslim Edildi"),
        CANCELLED("İptal Edildi");

        private final String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}