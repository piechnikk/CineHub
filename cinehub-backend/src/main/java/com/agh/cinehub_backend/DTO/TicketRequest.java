package com.agh.cinehub_backend.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequest {
    @NotNull(message = "ScreeningId cannot be empty")
    private Integer screeningId;

    @NotNull(message = "SeatId cannot be empty")
    private Integer seatId;
}
