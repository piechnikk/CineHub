package com.agh.cinehub_backend.service;

import com.agh.cinehub_backend.DTO.ScreeningRequest;
import com.agh.cinehub_backend.model.*;
import com.agh.cinehub_backend.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ScreeningService {
    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final TicketRepository ticketRepository;

    public void addScreening(ScreeningRequest request) {
        Room room = roomRepository.findByName(request.getRoomName()).orElseThrow(() -> new IllegalArgumentException("Room not found"));
        Movie movie = movieRepository.findById(request.getMovieId()).orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        if(request.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Screening start date cannot be in the past");
        }

        Screening screening = Screening.builder()
                .room(room)
                .movie(movie)
                .startDate(request.getStartDate())
                .price(request.getPrice())
                .build();

        // TODO: any error handling?
        screeningRepository.save(screening);

    }

    public List<Screening> getScreeningsByMovie(Movie movie) {
        return screeningRepository.findAllByMovie(movie);
    }

    public Screening getScreeningById(int screeningId) {
        return screeningRepository.findById(screeningId)
                .orElseThrow(() -> new IllegalArgumentException("Screening not found"));
    }

    public List<Seat> getTakenSeats(Integer screeningId) {
        Screening screening = getScreeningById(screeningId);

        return ticketRepository
                .findAllByScreening(screening)
                .stream()
                .filter(ticket -> !Statuses.CANCELED.getName().equals(ticket.getStatus().getName()))
                .map(Ticket::getSeat)
                .toList();
    }
}
