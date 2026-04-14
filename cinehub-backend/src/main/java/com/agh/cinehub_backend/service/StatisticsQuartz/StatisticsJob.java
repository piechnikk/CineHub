package com.agh.cinehub_backend.service.StatisticsQuartz;

import com.agh.cinehub_backend.model.Movie;
import com.agh.cinehub_backend.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class StatisticsJob implements Job {
    private final TicketRepository ticketRepository;

    private final StatisticsStorage statisticsStorage;

    @Override
    public void execute(JobExecutionContext jobExecutionContext){
        calculateMostPopularMoviesEver();
    }

    private void calculateMostPopularMoviesEver(){
        Map<Movie, Long> mostPopularMoviesMap = ticketRepository.findAll().stream()
                .collect(Collectors.groupingBy(t -> t.getScreening().getMovie(), Collectors.counting()));

        statisticsStorage.setMostPopularMoviesMap(mostPopularMoviesMap);

        List<Map.Entry<Movie, Long>> sortedEntries = mostPopularMoviesMap.entrySet().stream()
                .sorted(Map.Entry.<Movie, Long>comparingByValue().reversed())
                .toList();

        List<Movie> sortedMovies = sortedEntries.stream().map(e -> e.getKey()).toList();
        statisticsStorage.setMostPopularMoviesList(sortedMovies);
    }



}
