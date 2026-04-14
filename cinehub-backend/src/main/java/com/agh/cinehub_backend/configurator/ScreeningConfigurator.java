package com.agh.cinehub_backend.configurator;

import com.agh.cinehub_backend.model.Movie;
import com.agh.cinehub_backend.model.Room;
import com.agh.cinehub_backend.model.Screening;
import com.agh.cinehub_backend.repository.MovieRepository;
import com.agh.cinehub_backend.repository.RoomRepository;
import com.agh.cinehub_backend.repository.ScreeningRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class ScreeningConfigurator {
    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;

    public ScreeningConfigurator(ScreeningRepository screeningRepository, MovieRepository movieRepository, RoomRepository roomRepository) {
        this.screeningRepository = screeningRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
    }

    @PostConstruct
    public void init() {
        if (screeningRepository.count() == 0) {
            createMultipleScreenings("101", 20, LocalDate.of(2026, 12, 23));
            createMultipleScreenings("101", 20, LocalDate.of(2026, 12, 24));
            createMultipleScreenings("101", 20, LocalDate.of(2026, 12, 25));
            createMultipleScreenings("101", 20, LocalDate.of(2026, 12, 26));
            createMultipleScreenings("101", 20, LocalDate.of(2026, 12, 27));
            createMultipleScreenings("101", 20, LocalDate.of(2026, 12, 28));
            createMultipleScreenings("101", 20, LocalDate.of(2026, 12, 29));
            createMultipleScreenings("101", 20, LocalDate.of(2026, 12, 30));
            createMultipleScreenings("101", 20, LocalDate.of(2026, 12, 31));
            createMultipleScreenings("101", 20, LocalDate.of(2027, 1, 1));
            createMultipleScreenings("101", 20, LocalDate.of(2027, 1, 2));
            createMultipleScreenings("101", 20, LocalDate.of(2027, 1, 3));
            createMultipleScreenings("101", 20, LocalDate.of(2027, 1, 4));
            createMultipleScreenings("101", 20, LocalDate.of(2027, 1, 5));
            createMultipleScreenings("101", 20, LocalDate.of(2027, 1, 6));
            createMultipleScreenings("101", 20, LocalDate.of(2027, 1, 7));
            createMultipleScreenings("101", 20, LocalDate.of(2027, 1, 8));
            createMultipleScreenings("101", 20, LocalDate.of(2027, 1, 9));
            createMultipleScreenings("101", 20, LocalDate.of(2027, 1, 10));
            createMultipleScreenings("101", 20, LocalDate.of(2027, 1, 11));
        }
    }

    private void createScreening(Movie movie, Room room, float price, LocalDateTime startDate) {
        Screening screening = new Screening();
        screening.setMovie(movie);
        screening.setRoom(room);
        screening.setPrice(price);
        screening.setStartDate(startDate);
        screeningRepository.save(screening);
    }

    private void createMultipleScreenings(String roomName, float price, LocalDate date) {
        List<Integer> moviesId = movieRepository.getMoviesId();

        for (Integer id : moviesId) {
            Movie movie = movieRepository.findByMovieId(id).orElse(null);
            Room room = roomRepository.findByName(roomName).orElse(null);

            if (movie == null || room == null) continue;

            List<LocalDateTime> startDates = List.of(
                    LocalDateTime.of(date, LocalTime.of(12, 0)),
                    LocalDateTime.of(date, LocalTime.of(15, 0)),
                    LocalDateTime.of(date, LocalTime.of(18, 0)),
                    LocalDateTime.of(date, LocalTime.of(21, 0))
            );

            startDates.forEach(startDate -> {
                createScreening(movie, room, price, startDate);
            });
        }
    }
}
