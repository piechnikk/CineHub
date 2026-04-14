package com.agh.cinehub_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "screenings")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Screening {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer screeningId;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    private LocalDateTime startDate;

    @Column(nullable = false)
    private float price;

}

