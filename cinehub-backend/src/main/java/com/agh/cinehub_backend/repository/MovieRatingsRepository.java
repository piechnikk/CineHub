package com.agh.cinehub_backend.repository;

import com.agh.cinehub_backend.model.MovieRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieRatingsRepository extends JpaRepository<MovieRating, Integer> {


    @Query("SELECT r FROM MovieRating r WHERE r.movie.movieId = :movieId")
    Optional<MovieRating> getMovieRatingByMovieId(@Param("movieId") Integer movieId);

    @Query("SELECT r FROM MovieRating r ORDER BY r.averageRating DESC")
    Page<MovieRating> getSortedMovieRatings(Pageable pageable);

    @Query("SELECT r FROM MovieRating r ORDER BY r.averageRating DESC LIMIT :limit ")
    List<MovieRating> getSortedMovieRatingsWithLimit(@Param("limit") Integer limit);

}
