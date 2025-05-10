package erdalguda.main.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderRequest {
    private String status;
    private String notes;
    private Long selectedFabricId;
}