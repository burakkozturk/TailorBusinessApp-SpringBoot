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
    private CustomerShortResponse customer;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.productType = order.getProductType() != null ? order.getProductType().name() : null;
        this.fitType = order.getFitType() != null ? order.getFitType().name() : null;
        this.createdAt = order.getCreatedAt();
        this.orderDate = order.getOrderDate();
        this.estimatedDeliveryDate = order.getEstimatedDeliveryDate();
        this.deliveryDate = order.getDeliveryDate();
        this.status = order.getStatus() != null ? order.getStatus().name() : null;
        this.notes = order.getNotes();
        this.totalPrice = order.getTotalPrice();
        this.customer = order.getCustomer() != null ? new CustomerShortResponse(order.getCustomer()) : null;
    }
}