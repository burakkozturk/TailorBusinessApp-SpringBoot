package erdalguda.main.config;

import erdalguda.main.model.PatternTemplate;
import erdalguda.main.repository.PatternTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PatternTemplateSeeder {

    @Bean
    public CommandLineRunner seedPatternTemplates(PatternTemplateRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                PatternTemplate template1 = new PatternTemplate();
                template1.setName("36-SLIM GÖMLEK");
                template1.setProductType("GÖMLEK");
                template1.setFitType("SLIM");
                template1.setMinChest(82.0);
                template1.setMaxChest(88.0);
                template1.setMinWaist(64.0);
                template1.setMaxWaist(70.0);
                template1.setFileUrlPdf("/patterns/gomlek-36-slim.pdf");
                template1.setFileUrlDxf("/patterns/gomlek-36-slim.dxf");

                PatternTemplate template2 = new PatternTemplate();
                template2.setName("38-REGULAR CEKET");
                template2.setProductType("CEKET");
                template2.setFitType("REGULAR");
                template2.setMinChest(90.0);
                template2.setMaxChest(96.0);
                template2.setMinWaist(74.0);
                template2.setMaxWaist(80.0);
                template2.setFileUrlPdf("/patterns/ceket-38-regular.pdf");
                template2.setFileUrlDxf("/patterns/ceket-38-regular.dxf");

                PatternTemplate template3 = new PatternTemplate();
                template3.setName("40-BAGGY PANTOLON");
                template3.setProductType("PANTOLON");
                template3.setFitType("BAGGY");
                template3.setMinChest(95.0);
                template3.setMaxChest(105.0);
                template3.setMinWaist(84.0);
                template3.setMaxWaist(92.0);
                template3.setFileUrlPdf("/patterns/pantolon-40-baggy.pdf");
                template3.setFileUrlDxf("/patterns/pantolon-40-baggy.dxf");

                repo.save(template1);
                repo.save(template2);
                repo.save(template3);

                System.out.println("📦 Pattern templates seeded.");
            }
        };
    }
}
