package erdalguda.main.dto;

import erdalguda.main.model.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderResponse {
    private Long id;
    private String productType;
    private String fitType;
    private LocalDateTime createdAt;
    private LocalDate orderDate;
    private LocalDate estimatedDeliveryDate;
    private LocalDate deliveryDate;
    private String status;
    private String notes;
    private Double totalPrice;
    private String patternFilePath;
    private String patternFileType;

    private CustomerShortResponse customer;
    private FabricResponse fabric;

    public OrderResponse(Order o) {
        this.id = o.getId();
        this.productType = o.getProductType() != null ? o.getProductType().name() : null;
        this.fitType = o.getFitType() != null ? o.getFitType().name() : null;
        this.createdAt = o.getCreatedAt();
        this.orderDate = o.getOrderDate();
        this.estimatedDeliveryDate = o.getEstimatedDeliveryDate();
        this.deliveryDate = o.getDeliveryDate();
        this.status = o.getStatus() != null ? o.getStatus().name() : null;
        this.notes = o.getNotes();
        this.totalPrice = o.getTotalPrice();
        this.patternFilePath = o.getPatternFilePath();
        this.patternFileType = o.getPatternFileType();
        this.customer = o.getCustomer() != null ? new CustomerShortResponse(o.getCustomer()) : null;
        this.fabric = o.getFabric() != null ? new FabricResponse(o.getFabric()) : null;
    }
}