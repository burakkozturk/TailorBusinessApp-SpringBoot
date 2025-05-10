package erdalguda.main.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    private Long customerId;
    private String productType;
    private String fitType;
    private String status;
    private Long selectedFabricId;
    private String notes;
}
