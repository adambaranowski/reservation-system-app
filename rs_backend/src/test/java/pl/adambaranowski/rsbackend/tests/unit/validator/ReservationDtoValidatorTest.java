package pl.adambaranowski.rsbackend.tests.unit.validator;

import org.junit.jupiter.api.Test;
import pl.adambaranowski.rsbackend.exception.WrongDtoException;
import pl.adambaranowski.rsbackend.model.dto.CreateReservationRequestDto;
import pl.adambaranowski.rsbackend.validator.ReservationDtoValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.adambaranowski.rsbackend.tests.unit.TestUtils.*;
import static pl.adambaranowski.rsbackend.validator.ValidationConstants.*;

public class ReservationDtoValidatorTest {
    private final ReservationDtoValidator validator = new ReservationDtoValidator();

    @Test
    void validateDto_emptyFields_returnsErrors(){
        CreateReservationRequestDto wrongDto = new CreateReservationRequestDto();

        wrongDto.setRoomNumber(null);
        wrongDto.setBeginDate(null);
        wrongDto.setBeginTime(null);
        wrongDto.setDaysOfWeek(null);
        wrongDto.setEndDate(null);
        wrongDto.setEndTime(null);

        List<String> expectedErrorsList = List.of(
                NULL_ROOM_NUMBER,
                EMPTY_DATE,
                EMPTY_TIME,
                NULL_DAYS
        );

        WrongDtoException wrongDtoException = assertThrows(WrongDtoException.class, () -> validator.validateDto(wrongDto));
        List<String> actualErrorsList = wrongDtoException.getErrors();

        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void validateDto_wrongData_returnsErrors(){
        CreateReservationRequestDto wrongDto = new CreateReservationRequestDto();

        wrongDto.setRoomNumber(TEST_ROOM_NUMBER);
        wrongDto.setBeginDate(TEST_LATER_DATE);
        wrongDto.setBeginTime(TEST_LATER_TIME);
        wrongDto.setDaysOfWeek(TEST_WRONG_DAYS);
        wrongDto.setEndDate(TEST_PRIOR_DATE);
        wrongDto.setEndTime(TEST_PRIOR_TIME);

        List<String> expectedErrorsList = List.of(
                BEGIN_DATE_AFTER_END_DATE,
                BEGIN_TIME_AFTER_END_TIME,
                TEST_WRONG_DAYS.get(0) + ALLOWED_DAYS_MESSAGE
        );

        WrongDtoException wrongDtoException = assertThrows(WrongDtoException.class, () -> validator.validateDto(wrongDto));
        List<String> actualErrorsList = wrongDtoException.getErrors();

        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void validateDto_correctData(){
        CreateReservationRequestDto correctDto = new CreateReservationRequestDto();

        correctDto.setRoomNumber(TEST_ROOM_NUMBER);
        correctDto.setBeginDate(TEST_PRIOR_DATE);
        correctDto.setBeginTime(TEST_PRIOR_TIME);
        correctDto.setDaysOfWeek(TEST_CORRECT_DAYS);
        correctDto.setEndDate(TEST_LATER_DATE);
        correctDto.setEndTime(TEST_LATER_TIME);

        validator.validateDto(correctDto);
    }
}
