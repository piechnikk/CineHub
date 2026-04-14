package com.agh.cinehub_backend.controller;

import com.agh.cinehub_backend.model.Movie;
import com.agh.cinehub_backend.model.MovieRating;
import com.agh.cinehub_backend.service.MovieRatingsService;
import com.agh.cinehub_backend.service.StatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
@AllArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final MovieRatingsService movieRatingsService;

    @GetMapping("/getMostPopularMoviesEver/{quantity}")
    public List<Movie> getMostPopularMoviesEver(@PathVariable Integer quantity){
        if(quantity == null || quantity<1) quantity = 10;
        List<Movie> movies = statisticsService.getMostPopularMoviesWithListSize(quantity);

        return movies.stream().limit(quantity).toList();
    }

    @GetMapping("/soldTickets/{movieId}")
    public Map<LocalDate, Integer> getSoldTicketsStatistics(@PathVariable Integer movieId) {
        return statisticsService.getSoldTicketsStatistics(movieId);
    }

    @GetMapping("/getBestRatingMovies")
    public Page<MovieRating> getBestRatingMovies(@RequestParam(value="page") int page,
                                                 @RequestParam(value="size", defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return movieRatingsService.getMovieRatings(pageable);
    }

    @GetMapping("/getBestRatingMoviesWithLimit/{limit}")
    public List<MovieRating> getBestRatingMovies(@PathVariable Integer limit){

        return movieRatingsService.getMovieRatingsWithLimit(limit);
    }


    /** use this endpoint with caution it starts calculating all tickets statistics
     * for last 14 days, it may take some time
     */
    @GetMapping("/recalculateSoldTickets")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void recalculateSoldTickets(){
        statisticsService.recalculateTotalTicketsSoldFor14Days();
    }

}
