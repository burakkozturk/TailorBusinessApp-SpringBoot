package erdalguda.main.repository;

import erdalguda.main.model.PatternTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatternTemplateRepository extends JpaRepository<PatternTemplate, Long> {

    // Göğüs ve bel ölçüsüne göre filtreleme
    List<PatternTemplate> findByProductTypeAndFitTypeAndMinChestLessThanEqualAndMaxChestGreaterThanEqualAndMinWaistLessThanEqualAndMaxWaistGreaterThanEqual(
            String productType,
            String fitType,
            Double chest,
            Double chest2,
            Double waist,
            Double waist2
    );
}
