package erdalguda.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
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
    private String ocrMeasurementText;
    private String suggestedTemplate; // ✅ Yeni alan

    private String status;
    private Long selectedFabricId;
    private String notes;

    private FabricSummary fabric;


}

