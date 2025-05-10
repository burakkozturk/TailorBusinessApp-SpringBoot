package erdalguda.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FabricSummary {
    private Long id;
    private String name;
    private String texture;
    private String description;
    private String imageUrl;
}
