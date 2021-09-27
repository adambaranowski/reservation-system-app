package pl.adambaranowski.rsbackend.validator;

import org.springframework.stereotype.Component;
import pl.adambaranowski.rsbackend.exception.WrongDtoException;
import pl.adambaranowski.rsbackend.model.dto.CreateReservationRequestDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static pl.adambaranowski.rsbackend.validator.ValidationConstants.*;

@Component
public class ReservationDtoValidator {

    public void validateDto(CreateReservationRequestDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getRoomNumber() == null) {
            errors.add(NULL_ROOM_NUMBER);
        }

        try {
            if (dto.getBeginDate() == null || dto.getBeginDate().isBlank()
            || dto.getEndDate() == null || dto.getEndDate().isBlank()) {
                errors.add(EMPTY_DATE);
            } else {
                LocalDate beginDate = LocalDate.parse(dto.getBeginDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                    LocalDate endDate = LocalDate.parse(dto.getEndDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    if (beginDate.isAfter(endDate)) {
                        errors.add(BEGIN_DATE_AFTER_END_DATE);
                    }

            }

            if (dto.getBeginTime() == null || dto.getBeginTime().isBlank()
                    || dto.getEndDate() == null || dto.getEndTime().isBlank()) {
                errors.add(EMPTY_TIME);
            } else {
                LocalTime beginTime = LocalTime.parse(dto.getBeginTime(), DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime endTime = LocalTime.parse(dto.getEndTime(), DateTimeFormatter.ofPattern("HH:mm"));
                if (beginTime.isAfter(endTime)) {
                    errors.add(BEGIN_TIME_AFTER_END_TIME);
                }
            }

        } catch (DateTimeParseException parseException) {
            errors.add(parseException.getMessage());
        }

        if(dto.getDaysOfWeek() == null) {
            errors.add(NULL_DAYS);
        } else {
            if (!dto.getDaysOfWeek().isEmpty()) {
                dto.getDaysOfWeek().forEach(
                        day -> {
                            if (day > 5 || day < 1) {
                                errors.add(day + ALLOWED_DAYS_MESSAGE);
                            }
                        }
                );
            }
        }

        if (!errors.isEmpty()) {
            throw new WrongDtoException(errors);
        }
    }
}
