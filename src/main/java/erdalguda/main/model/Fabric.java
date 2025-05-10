package erdalguda.main.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fabrics")
@Getter
@Setter
public class Fabric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;            // Örnek: "Baby Blue Cashmere"
    private String texture;         // Dokusu: "Yumuşak", "Kabarık", "İnce"
    private String description;     // Açıklama: "Kışlık, lüks görünüm"
    private String imageUrl;        // Görsel bağlantısı (ileride kullanılacak)
}
