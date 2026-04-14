package com.agh.cinehub_backend.service;

import com.agh.cinehub_backend.DTO.ReviewRequest;
import com.agh.cinehub_backend.model.Movie;
import com.agh.cinehub_backend.model.Review;
import com.agh.cinehub_backend.model.User;
import com.agh.cinehub_backend.repository.MovieRepository;
import com.agh.cinehub_backend.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;

    public void addReview(User user, ReviewRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        if(reviewRepository.findByUserAndMovie(user, movie).isPresent()) {
            throw new IllegalArgumentException("User already reviewed this movie.");
        }

        Review review = Review.builder()
                    .user(user)
                    .movie(movie)
                    .score(request.getScore())
                    .description(request.getDescription())
                    .build();

        if (request.getScore() < 0 || request.getScore() > 5) {
            throw new IllegalArgumentException("Score must be between 0 and 5.");
        }

        if(request.getDescription().length() > 150 || request.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description is empty or too long. Max length is 150 characters.");
        }

        // TODO: any error handling?
        reviewRepository.save(review);
    }

    public List<Review> getReviewsByMovie(Movie movie) {
        return reviewRepository.findAllByMovie(movie);
    }

    public List<Review> getReviewsByUser(User user) {
        return  reviewRepository.findAllByUser(user);
    }

    public Double getRatingForFilm(Integer movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new IllegalArgumentException("Movie not found"));
        List<Review> reviews = this.getReviewsByMovie(movie);


        return Math.round(
                reviews.stream()
                .mapToDouble(Review::getScore)
                .average()
                .orElseThrow(() -> new IllegalArgumentException("No reviews found"))
                * 100.0) / 100.0;

    }
}
