package com.agh.cinehub_backend.service;

import com.agh.cinehub_backend.DTO.MovieRequest;
import com.agh.cinehub_backend.model.Genre;
import com.agh.cinehub_backend.model.Movie;
import com.agh.cinehub_backend.model.Tmdb;
import com.agh.cinehub_backend.repository.GenreRepository;
import com.agh.cinehub_backend.repository.MovieRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final GenreService genreService;
    private Tmdb tmdb;
    private WebClient webClient;

    @PostConstruct
    public void initWebClient() {
        this.webClient = WebClient.builder().baseUrl(tmdb.getApiUrl()).build();
    }

    public void addMovie(MovieRequest request) {
        Genre genre = genreService.findByName(request.getGenreName());

        // TODO: any error handling? empty data inserts into table
        if (movieRepository.findByTitle(request.getTitle()).isEmpty()) {
            Movie newFilm = Movie.builder()
                    .description(request.getDescription())
                    .title(request.getTitle())
                    .director(request.getDirector())
                    .duration(request.getDuration())
                    .publishDate(request.getPublishDate())
                    .production(request.getProduction())
                    .genre(genre)
                    .thumbnail_img(request.getThumbnailUrl())
                    .bg_img(request.getBgUrl())
                    .build();
            movieRepository.save(newFilm);
        }

    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public List<Movie> getMoviesByGenre(Genre genre) {
        return movieRepository.findAllByGenre(genre);
    }

    public Movie getMovieById(int id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
    }

    // TODO: improve logic
    public List<Movie> getTrendingMovies() {
        List<Movie> movies = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            movies.add(this.getMovieById(i));
        }
        return movies;
    }

    // TODO: major code refactor needed 
    @Transactional
    public void fetchMovies() {
        webClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/discover/movie")
                                .queryParam("api_key", tmdb.getApiKey())
                                .queryParam("language", "en-US")
                                .queryParam("sort_by", "popularity.desc")
                                .queryParam("page", 1)
                                .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> (List<Map<String, Object>>) response.get("results"))
                .flatMapMany(Flux::fromIterable)
                .flatMap(movieData -> {
                    try {
                        String title = (String) movieData.get("title");
                        String overview = (String) movieData.get("overview");
                        String bgUrl = (String) movieData.get("backdrop_path");
                        String thumbnailUrl = (String) movieData.get("poster_path");
                        LocalDate release = LocalDate.parse((String) movieData.get("release_date"));
                        Integer genreId = ((List<Integer>) movieData.get("genre_ids")).get(0);

                        String genreName = genreRepository.findNameById(genreId)
                                .orElseThrow(() -> new IllegalArgumentException("Genre not found"));

                        Integer movieId = (Integer) movieData.get("id");

                        return webClient
                                .get()
                                .uri(uriBuilder ->
                                        uriBuilder
                                                .path("/movie/" + movieId)
                                                .queryParam("api_key", tmdb.getApiKey())
                                                .queryParam("language", "en-US")
                                                .build()
                                )
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                                .map(movieDetails -> {
                                    List<Map<String, Object>> productionCompanies = (List<Map<String, Object>>) movieDetails.get("production_companies");
                                    String productionCompanyName = productionCompanies.stream()
                                            .findFirst()
                                            .map(company -> (String) company.get("name"))
                                            .orElse("Unknown");

                                    Integer runtime = (Integer) movieDetails.get("runtime");

                                    return MovieRequest.builder()
                                            .title(title)
                                            .description(overview)
                                            .publishDate(release)
                                            .genreName(genreName)
                                            .bgUrl("https://image.tmdb.org/t/p/original" + bgUrl)
                                            .thumbnailUrl("https://image.tmdb.org/t/p/w500" + thumbnailUrl)
                                            .director("Kamil Rudny")
                                            .duration(runtime != null ? runtime : 120)
                                            .production(productionCompanyName)
                                            .build();
                                });
                    } catch (Exception e) {
                        System.err.println("Error processing movie: " + movieData.get("title") + ". Details: " + e.getMessage());
                        return Mono.empty();
                    }
                })
                .doOnNext(this::addMovie)
                .onErrorContinue((error, item) -> System.err.println("Error adding movie. Details: " + error.getMessage()))
                .blockLast();
    }

    public Page<Movie> getPagedMovies(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    public Page<Movie> getPagedMoviesWithSearch(Pageable pageable, String name) {
        return movieRepository.findByTitleContainingIgnoreCase(name, pageable);
    }
}
