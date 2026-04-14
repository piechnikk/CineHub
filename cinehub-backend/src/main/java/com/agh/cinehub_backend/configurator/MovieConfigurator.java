package com.agh.cinehub_backend.configurator;

import com.agh.cinehub_backend.model.Genre;
import com.agh.cinehub_backend.model.Movie;
import com.agh.cinehub_backend.repository.GenreRepository;
import com.agh.cinehub_backend.repository.MovieRepository;
import com.agh.cinehub_backend.service.MovieService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.time.LocalDate;

@Configuration
public class MovieConfigurator {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final MovieService movieService;

    public MovieConfigurator(MovieRepository movieRepository, GenreRepository genreRepository, MovieService movieService) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.movieService = movieService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        movieService.fetchMovies();
    }

//    @PostConstruct
    public void init() {
        if (movieRepository.count() == 0) {
            createMovie("Inception", "A skilled thief is given a chance at redemption if he can successfully perform an inception.", "Christopher Nolan", "Warner Bros", 148, LocalDate.of(2010, 7, 16), "Action");
            createMovie("The Matrix", "A computer hacker learns about the true nature of his reality and his role in a war against its controllers.", "The Wachowskis", "Warner Bros", 136, LocalDate.of(1999, 3, 31), "Sci-Fi");
        }
    }

    private void createMovie(String title, String description, String director, String production, int duration, LocalDate publishDate, String genreName) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setDirector(director);
        movie.setProduction(production);
        movie.setDuration(duration);
        movie.setPublishDate(publishDate);
        Genre genre = genreRepository.findByName(genreName).orElse(null);

        if (genre == null) return;

        movie.setGenre(genre);
        movieRepository.save(movie);
    }
}
