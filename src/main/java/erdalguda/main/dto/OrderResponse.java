package erdalguda.main.dto;

        import erdalguda.main.model.Fabric;
        import erdalguda.main.model.Measurement;
        import lombok.AllArgsConstructor;
        import lombok.Getter;
        import lombok.NoArgsConstructor;
        import lombok.Setter;

        import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;
    private String productType;
    private String fitType;
    private LocalDate orderDate;
    private Long customerId;
    private String customerName;
    private Double height;
    private Double weight;
    private Measurement measurement;  // Bu eklendi
    private String suggestedTemplate;
    private String status;
    private Long selectedFabricId;
    private String notes;
    private Fabric fabric;

    // Constructor, getter, setter...

    public OrderResponse(Long orderId, String productType, String fitType, LocalDate orderDate,
                         Long customerId, String customerName, Double height, Double weight,
                         Measurement measurement, String suggestedTemplate, String status,
                         Long selectedFabricId, String notes, Fabric fabric) {
        this.orderId = orderId;
        this.productType = productType;
        this.fitType = fitType;
        this.orderDate = orderDate;
        this.customerId = customerId;
        this.customerName = customerName;
        this.height = height;
        this.weight = weight;
        this.measurement = measurement;
        this.suggestedTemplate = suggestedTemplate;
        this.status = status;
        this.selectedFabricId = selectedFabricId;
        this.notes = notes;
        this.fabric = fabric;
    }

    // Getter / Setter...
}
