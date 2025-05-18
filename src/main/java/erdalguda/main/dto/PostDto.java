package erdalguda.main.dto;

import erdalguda.main.model.Category;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostDto {
    private Long id;
    private String title;
    private String urlSlug;
    private String content;
    private String featuredImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CategoryDto> categories = new ArrayList<>();
    private Boolean published = false;
}