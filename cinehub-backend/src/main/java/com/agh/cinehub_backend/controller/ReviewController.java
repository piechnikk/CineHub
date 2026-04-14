package com.agh.cinehub_backend.controller;

import com.agh.cinehub_backend.DTO.ReviewRequest;
import com.agh.cinehub_backend.model.Movie;
import com.agh.cinehub_backend.model.Review;
import com.agh.cinehub_backend.model.User;
import com.agh.cinehub_backend.service.MovieService;
import com.agh.cinehub_backend.service.ReviewService;
import com.agh.cinehub_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final MovieService movieService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'USER') or  #movieId != null")
    public List<Review> getReviews(@RequestParam(value = "movieId", required = false) Integer movieId) {
        if (movieId != null) {
            Movie movie = movieService.getMovieById(movieId);
            return reviewService.getReviewsByMovie(movie);
        } else {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(userEmail);
            return reviewService.getReviewsByUser(user);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'USER')")
    public ResponseEntity<?> addReview(@Valid @RequestBody ReviewRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(userEmail);
        Movie movie = movieService.getMovieById(request.getMovieId());

        reviewService.addReview(user, request);

        return ResponseEntity.ok("Review for film " + movie.getTitle() + " added successfully!");
    }

    @GetMapping("/rating/{movieId}")
    public ResponseEntity<Double> getRatingForFilm(@PathVariable Integer movieId) {
        Double result = reviewService.getRatingForFilm(movieId);
        return ResponseEntity.ok(result);
    }
}
