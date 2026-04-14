package com.agh.cinehub_backend.configurator;

import com.agh.cinehub_backend.model.Genre;
import com.agh.cinehub_backend.repository.GenreRepository;
import com.agh.cinehub_backend.service.GenreService;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@AllArgsConstructor
public class GenreConfigurator {
    private final GenreRepository genreRepository;
    private final GenreService genreService;


    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        genreService.fetchGenres();
    }

//    @PostConstruct
    public void init() {
        if (genreRepository.count() == 0) {
            createGenre("Action");
            createGenre("Sci-Fi");
        }
    }

    private void createGenre(String genreName) {
        Genre genre = new Genre();
        genre.setName(genreName);
        genreRepository.save(genre);
    }
}
