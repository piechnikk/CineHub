package com.agh.cinehub_backend.controller;

import com.agh.cinehub_backend.DTO.GenreRequest;
import com.agh.cinehub_backend.model.Genre;
import com.agh.cinehub_backend.service.GenreService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<Genre> getGenres() {
        return genreService.getAllGenres();
    }

    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> addGenre(@Valid @RequestBody GenreRequest request) {
        genreService.addGenre(request);
        return ResponseEntity.ok("Genre with name " + request.getName() + " added successfully!");
    }
}
