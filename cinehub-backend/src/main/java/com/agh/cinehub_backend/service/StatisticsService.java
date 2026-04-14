package com.agh.cinehub_backend.service;

import com.agh.cinehub_backend.model.Movie;
import com.agh.cinehub_backend.repository.DailyTicketsStatisticsRepository;
import com.agh.cinehub_backend.repository.MovieRepository;
import com.agh.cinehub_backend.repository.TicketRepository;
import com.agh.cinehub_backend.repository.UserRepository;
import com.agh.cinehub_backend.service.StatisticsQuartz.DailyTotalTicketCounterJob;
import com.agh.cinehub_backend.service.StatisticsQuartz.StatisticsStorage;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Data
@AllArgsConstructor
public class StatisticsService {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TicketRepository ticketRepository;
    private final StatisticsStorage statisticsStorage;
    private final DailyTotalTicketCounterJob dailyTotalTicketCounterJob;
    private final DailyTicketsStatisticsRepository dailyTicketsStatisticsRepository;


    public Map<Movie, Long> getMostPopularMoviesMap(){
        return statisticsStorage.getMostPopularMoviesMap();
    }


    public void recalculateTotalTicketsSoldFor14Days(){
        dailyTotalTicketCounterJob.calculateTotalTicketsForLast14Days();
    }



    public List<Movie> getMostPopularMoviesList(){
        List<Movie> mostPopularMoviesList = statisticsStorage.getMostPopularMoviesList();
        if(mostPopularMoviesList == null){
            throw new RuntimeException("Statistics are not calculated yet");
        }
        return mostPopularMoviesList;
    }

    public List<Movie> getMostPopularMoviesWithListSize(int size){
        List<Movie> movies = new ArrayList<>(getMostPopularMoviesList());
        if(movies.size()>=size){
            return movies.stream().limit(size).toList();
        }
        List<Integer> moviesIds = movieRepository.getMoviesId();

        int numberOfMoviesToAdd = size-movies.size();
        List<Integer> indexesMoviesOfStats = movies.stream().map(m -> m.getMovieId()).toList();
        if(numberOfMoviesToAdd>0 && moviesIds.size() > movies.size() + numberOfMoviesToAdd){
                for(int movieId: moviesIds){
                    if(numberOfMoviesToAdd == 0){
                        break;
                    }
                    if(!indexesMoviesOfStats.contains(movieId)){
                        movies.add(movieRepository.findByMovieId(movieId).get());
                        numberOfMoviesToAdd--;
                    }
                }
        }
        return movies;

    }

    public Map<LocalDate, Integer> getSoldTicketsStatistics(Integer movieId) {

//        Map<LocalDate, Integer> ticketsByDate = ticketRepository.findTicketsByMovieId(movieId)
//                .stream().collect(Collectors.groupingBy(
//                        ticket -> ticket.getScreening().getStartDate().toLocalDate(),
//                        Collectors.summingInt(ticket -> 1)
//                ));


        // LocalDate today = LocalDate.now();
//        return IntStream.rangeClosed(0, 13)
//                .mapToObj(today::minusDays)
//                .collect(Collectors.toMap(
//                        date -> date,
//                        date -> ticketsByDate.getOrDefault(date, 0)
//                ));

        LocalDate startDay = LocalDate.now().minusDays(14).atStartOfDay().toLocalDate();
        LocalDate endDay = LocalDate.now().atStartOfDay().toLocalDate();
        Map<LocalDate, Integer> ticketStats = dailyTicketsStatisticsRepository.findByIdMovieIdAndIdDateBetween(movieId, startDay, endDay)
                .stream().collect(Collectors.toMap(
                        stat -> stat.getId().getDate(),
                        stat -> stat.getTotalDailyTickets()));

        return ticketStats;
    }

}
