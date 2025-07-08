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

    // Manual getter/setter methods (Lombok not working)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public ProductType getProductType() { return productType; }
    public void setProductType(ProductType productType) { this.productType = productType; }
    
    public FitType getFitType() { return fitType; }
    public void setFitType(FitType fitType) { this.fitType = fitType; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    
    public LocalDate getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }
    
    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}