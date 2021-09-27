package pl.adambaranowski.rsbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.adambaranowski.rsbackend.api.ReservationsApi;
import pl.adambaranowski.rsbackend.model.dto.CreateReservationRequestDto;
import pl.adambaranowski.rsbackend.model.dto.GetReservationRequestDto;
import pl.adambaranowski.rsbackend.model.dto.SingleReservationDto;
import pl.adambaranowski.rsbackend.service.ReservationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationController implements ReservationsApi {
    private final ReservationService reservationService;

    @Override
    public ResponseEntity<Void> addReservation(CreateReservationRequestDto createReservationRequestDto) {
        reservationService.addReservation(createReservationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> deleteReservation(Integer reservationId) {
        reservationService.deleteReservation(reservationId);
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<List<SingleReservationDto>> getReservationsForRoom(GetReservationRequestDto getReservationRequestDto) {
        return ResponseEntity.ok(reservationService.getReservationsForPeriodForRoom(getReservationRequestDto));
    }
}
