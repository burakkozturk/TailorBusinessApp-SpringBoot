package erdalguda.main.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pattern_templates")
@Getter
@Setter
public class PatternTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;         // Örnek: "Beden 36 Slim Gömlek"
    private String productType;  // GÖMLEK, CEKET, PANTOLON
    private String fitType;      // SLIM, REGULAR, BAGGY

    private Double minChest;
    private Double maxChest;

    private Double minWaist;
    private Double maxWaist;

    private String fileUrlPdf;
    private String fileUrlDxf;
}
