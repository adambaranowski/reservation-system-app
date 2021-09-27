package pl.adambaranowski.rsbackend.tests.unit.validator;

import org.junit.jupiter.api.Test;
import pl.adambaranowski.rsbackend.exception.WrongDtoException;
import pl.adambaranowski.rsbackend.model.dto.RoomRequestDto;
import pl.adambaranowski.rsbackend.validator.RoomDtoValidator;

import java.util.List;

import static pl.adambaranowski.rsbackend.validator.ValidationConstants.*;
import static pl.adambaranowski.rsbackend.tests.unit.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RoomDtoValidatorTest {

    private final RoomDtoValidator validator = new RoomDtoValidator();

    @Test
    void validateDto_emptyFields_returnsErrors(){

        RoomRequestDto wrongDto = new RoomRequestDto();

        wrongDto.setDescription(null);
        wrongDto.setRoomNumber(null);
        wrongDto.setRoomStatus(null);

        List<String> expectedErrorsList = List.of(
                NULL_ROOM_NUMBER,
                NULL_ROOM_STATUS,
                BLANK_DESCRIPTION
        );

        WrongDtoException wrongDtoException = assertThrows(WrongDtoException.class, () -> validator.validate(wrongDto));
        List<String> actualErrorsList = wrongDtoException.getErrors();

        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void validateDto_wrongData_returnsErrors(){

        RoomRequestDto wrongDto = new RoomRequestDto();

        wrongDto.setDescription(generateTestString(ROOM_DESCRIPTION_MAX_LENGTH + 1));
        wrongDto.setRoomNumber(TEST_WRONG_ROOM_NUMBER);
        wrongDto.setRoomStatus(RoomRequestDto.RoomStatusEnum.NORMAL);

        List<String> expectedErrorsList = List.of(
                WRONG_ROOM_NUMBER,
                TOO_LONG_DESCRIPTION
        );

        WrongDtoException wrongDtoException = assertThrows(WrongDtoException.class, () -> validator.validate(wrongDto));
        List<String> actualErrorsList = wrongDtoException.getErrors();

        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void validateDto_correctData(){

        RoomRequestDto correctDto = new RoomRequestDto();

        correctDto.setDescription(generateTestString(ROOM_DESCRIPTION_MAX_LENGTH));
        correctDto.setRoomNumber(TEST_ROOM_NUMBER);
        correctDto.setRoomStatus(RoomRequestDto.RoomStatusEnum.NORMAL);

        validator.validate(correctDto);
    }

}
