package com.agh.cinehub_backend.service;

import com.agh.cinehub_backend.DTO.GenreRequest;
import com.agh.cinehub_backend.model.Genre;
import com.agh.cinehub_backend.model.Tmdb;
import com.agh.cinehub_backend.repository.GenreRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private WebClient webClient;
    private final Tmdb tmdb;

    @PostConstruct
    private void initWebClient() {
        this.webClient = WebClient.builder()
                .baseUrl(tmdb.getApiUrl())
                .build();
    }

    @Transactional
    public void fetchGenres() {
        Map<String, Object> response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/genre/movie/list")
                        .queryParam("language", "en-US")
                        .queryParam("api_key", tmdb.getApiKey())
                        .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (response != null && response.containsKey("genres")) {
            List<Map<String, Object>> genres = (List<Map<String, Object>>) response.get("genres");
            genres.forEach(genreData -> {
                Integer id = (Integer) genreData.get("id");
                String name = (String) genreData.get("name");

                if (!genreRepository.existsById(id)) {
                    Genre newGenre = Genre.builder()
                            .genreId(id)
                            .name(name)
                            .build();

                    try {
                        genreRepository.save(newGenre);
                    } catch (DataIntegrityViolationException e) {
                        System.err.println("Genre already exists: " + name);
                    }
                } else {
                    System.out.println("Genre already exists: " + name);
                }
            });
        } else {
            System.err.println("No genres found in the response.");
        }
    }


    public Genre findByName(String name) {
        return genreRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Genre with name '" + name + "' doesn't exist."));
    }

    public void addGenre(GenreRequest request) {
        Genre newGenre = Genre.builder()
                .name(request.getName())
                .build();

        try {
            genreRepository.save(newGenre);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Genre with name '" + request.getName() + "' already exists.");
        }
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
}
