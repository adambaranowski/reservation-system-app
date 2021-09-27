package pl.adambaranowski.rsbackend.tests.unit.validator;

import org.junit.jupiter.api.Test;
import pl.adambaranowski.rsbackend.exception.WrongDtoException;
import pl.adambaranowski.rsbackend.model.dto.EquipmentRequestDto;
import pl.adambaranowski.rsbackend.validator.EquipmentDtoValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.adambaranowski.rsbackend.tests.unit.TestUtils.*;
import static pl.adambaranowski.rsbackend.validator.ValidationConstants.*;

class EquipmentDtoValidatorTest {
    private final EquipmentDtoValidator validator = new EquipmentDtoValidator();


    @Test
    void validateDto_emptyFields_returnsErrors() {
        EquipmentRequestDto wrongDto = new EquipmentRequestDto();

        wrongDto.setName("");
        wrongDto.setDescription(null);
        wrongDto.setRoomNumber(null);

        List<String> expectedErrorsList = List.of(
                EMPTY_NAME,
                EMPTY_DESCRIPTION,
                NULL_ROOM_NUMBER
        );

        WrongDtoException wrongDtoException = assertThrows(WrongDtoException.class, () -> validator.validate(wrongDto));
        List<String> actualErrorsList = wrongDtoException.getErrors();

        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void validateDto_tooLongStrings_returnsErrors(){
        EquipmentRequestDto wrongDto = new EquipmentRequestDto();

        wrongDto.setName(generateTestString(NAME_MAX_LENGTH + 1));
        wrongDto.setDescription(generateTestString(DESCRIPTION_MAX_LENGTH + 1));
        wrongDto.setRoomNumber(TEST_ROOM_NUMBER);

        List<String> expectedErrorsList = List.of(
                TOO_LONG_NAME,
                TOO_LONG_DESCRIPTION
        );

        WrongDtoException wrongDtoException = assertThrows(WrongDtoException.class, () -> validator.validate(wrongDto));
        List<String> actualErrorsList = wrongDtoException.getErrors();

        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void validateDto_correctData(){
        EquipmentRequestDto correctDto = new EquipmentRequestDto();

        correctDto.setName(generateTestString(NAME_MAX_LENGTH));
        correctDto.setDescription(generateTestString(DESCRIPTION_MAX_LENGTH));
        correctDto.setRoomNumber(TEST_ROOM_NUMBER);

        validator.validate(correctDto);
    }
}