package erdalguda.main.dto;

import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private String urlSlug;
    private String description;
}