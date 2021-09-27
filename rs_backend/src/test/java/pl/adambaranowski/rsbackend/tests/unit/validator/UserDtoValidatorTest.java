package pl.adambaranowski.rsbackend.tests.unit.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import pl.adambaranowski.rsbackend.exception.WrongDtoException;
import pl.adambaranowski.rsbackend.model.dto.UserRequestDto;
import pl.adambaranowski.rsbackend.validator.UserDtoValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.adambaranowski.rsbackend.tests.unit.TestUtils.*;
import static pl.adambaranowski.rsbackend.validator.UserDtoValidator.PASSWORD_INSTRUCTIONS;
import static pl.adambaranowski.rsbackend.validator.ValidationConstants.*;


public class UserDtoValidatorTest {

    private final UserDtoValidator validator = new UserDtoValidator();

    @Test
    void validate_emptyFields_returnsErrors(){
        UserRequestDto wrongDto = new UserRequestDto();

        wrongDto.setAuthorities(null);
        wrongDto.setEmail(null);
        wrongDto.setUserNick(null);
        wrongDto.setPassword(null);

        List<String> expectedErrorsList = List.of(
                PASSWORD_INSTRUCTIONS,
                EMPTY_EMAIL,
                NO_AUTHORITY,
                EMPTY_NICK
        );

        WrongDtoException wrongDtoException = assertThrows(WrongDtoException.class, () -> validator.validateUserDto(wrongDto));
        List<String> actualErrorsList = wrongDtoException.getErrors();

        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void validate_nickTooLong_returnsErrors(){
        UserRequestDto wrongDto = new UserRequestDto();

        wrongDto.setAuthorities(TEST_AUTHORITIES);
        wrongDto.setEmail(TEST_VALID_EMAIL_ADDRESS);
        wrongDto.setUserNick(generateTestString(NICK_MAX_LENGTH + 1));
        wrongDto.setPassword(TEST_VALID_PASSWORD);

        List<String> expectedErrorsList = List.of(TOO_LONG_NICK);

        WrongDtoException wrongDtoException = assertThrows(WrongDtoException.class, () -> validator.validateUserDto(wrongDto));
        List<String> actualErrorsList = wrongDtoException.getErrors();

        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @ParameterizedTest
    @EnumSource(InvalidPasswords.class)
    void validate_wrongPassword_returnsErrors(InvalidPasswords password){
        UserRequestDto wrongDto = new UserRequestDto();

        wrongDto.setAuthorities(TEST_AUTHORITIES);
        wrongDto.setEmail(TEST_VALID_EMAIL_ADDRESS);
        wrongDto.setUserNick(generateTestString(NICK_MAX_LENGTH));
        wrongDto.setPassword(password.getPassword());

        List<String> expectedErrorsList = List.of(PASSWORD_INSTRUCTIONS);

        WrongDtoException wrongDtoException = assertThrows(WrongDtoException.class, () -> validator.validateUserDto(wrongDto));
        List<String> actualErrorsList = wrongDtoException.getErrors();

        assertEquals(expectedErrorsList, actualErrorsList);

    }

    @ParameterizedTest
    @EnumSource(InvalidEmailAddresses.class)
    void validate_wrongEmail_returnsErrors(InvalidEmailAddresses email){
        UserRequestDto wrongDto = new UserRequestDto();

        wrongDto.setAuthorities(TEST_AUTHORITIES);
        wrongDto.setEmail(email.getEmail());
        wrongDto.setUserNick(generateTestString(NICK_MAX_LENGTH));
        wrongDto.setPassword(TEST_VALID_PASSWORD);

        List<String> expectedErrorsList = List.of(WRONG_EMAIL_PATTERN);

        WrongDtoException wrongDtoException = assertThrows(WrongDtoException.class, () -> validator.validateUserDto(wrongDto));
        List<String> actualErrorsList = wrongDtoException.getErrors();

        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void validate_correctData(){
        UserRequestDto correctDto = new UserRequestDto();

        correctDto.setAuthorities(TEST_AUTHORITIES);
        correctDto.setEmail(TEST_VALID_EMAIL_ADDRESS);
        correctDto.setUserNick(generateTestString(NICK_MAX_LENGTH));
        correctDto.setPassword(TEST_VALID_PASSWORD);

        validator.validateUserDto(correctDto);
    }
}
