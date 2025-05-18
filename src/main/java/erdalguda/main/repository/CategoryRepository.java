package erdalguda.main.repository;

import erdalguda.main.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByUrlSlug(String urlSlug);
}