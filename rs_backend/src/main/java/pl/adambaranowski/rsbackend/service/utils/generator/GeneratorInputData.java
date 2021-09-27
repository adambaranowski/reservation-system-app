package pl.adambaranowski.rsbackend.service.utils.generator;

import lombok.Getter;
import pl.adambaranowski.rsbackend.model.Reservation;
import pl.adambaranowski.rsbackend.model.Room;
import pl.adambaranowski.rsbackend.model.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@Getter
public class GeneratorInputData {
    private final Set<DayOfWeek> weekDays;

    private final Reservation reservation;

    private final LocalDate requestedStartDate;
    private final LocalDate requestedEndDate;

    private final Integer reservationId;
    private final User user;
    private final Room room;

    public GeneratorInputData(Reservation reservation, LocalDate requestedStartDate, LocalDate requestedEndDate) {
        this.reservation = reservation;
        this.requestedStartDate = requestedStartDate;
        this.requestedEndDate = requestedEndDate;
        this.reservationId = reservation.getId();
        this.user = reservation.getUser();
        this.room = reservation.getRoom();

        if (reservation.getRecurrencePattern() != null) {
            this.weekDays = reservation.getRecurrencePattern().getWeekDays();
        }else {
            weekDays = null;
        }
    }

    public LocalDate getStartDate() {
        return reservation.getBeginDate().isBefore(requestedStartDate)
                ? requestedStartDate
                : reservation.getBeginDate();
    }

    public LocalDate getEndDate() {
        return reservation.getEndDate().isBefore(requestedEndDate)
                ? reservation.getEndDate().plusDays(1)
                : requestedEndDate.plusDays(1);
    }

}
