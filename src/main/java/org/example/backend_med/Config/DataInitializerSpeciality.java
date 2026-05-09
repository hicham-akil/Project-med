package org.example.backend_med.Config;

import org.example.backend_med.Models.Specialite;
import org.example.backend_med.Repository.SpecialiteRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializerSpeciality {

    @Bean
    CommandLineRunner initDatabase(SpecialiteRepo repo) {
        return args -> {

            insertIfNotExists(repo, "Cardiologie");
            insertIfNotExists(repo, "Dermatologie");
            insertIfNotExists(repo, "Ophtalmologie");
            insertIfNotExists(repo, "Pediatrie");
        };
    }

    private void insertIfNotExists(SpecialiteRepo repo, String name) {
        if (!repo.existsByNomspecialite(name)) {
            repo.save(new Specialite(name));
        }
    }
}