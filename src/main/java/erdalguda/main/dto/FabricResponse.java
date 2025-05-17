package erdalguda.main.dto;

import erdalguda.main.model.Fabric;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FabricResponse {
    private Long id;
    private String name;

    public FabricResponse(Fabric f) {
        this.id = f.getId();
        this.name = f.getName();
    }
}