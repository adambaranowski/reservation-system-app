package pl.adambaranowski.rsbackend.validator;

import org.springframework.stereotype.Component;
import pl.adambaranowski.rsbackend.exception.WrongDtoException;
import pl.adambaranowski.rsbackend.model.dto.UserRequestDto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static pl.adambaranowski.rsbackend.validator.ValidationConstants.*;

@Component
public class UserDtoValidator {

    public static final String PASSWORD_INSTRUCTIONS = "\n" +
            "a digit must occur at least once\n" +
            "a lower case letter must occur at least once\n" +
            "an upper case letter must occur at least once\n" +
            "a special character must occur at least once\n" +
            "no whitespace allowed in the entire string\n" +
            "anything, at least eight places though\n";

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    public void validateUserDto(UserRequestDto dto) {
        List<String> errors = new ArrayList<>();

        if (!isPasswordValid(dto)) {
            errors.add(PASSWORD_INSTRUCTIONS);
        }

        if (!isEmailValid(dto)) {
            errors.add(WRONG_EMAIL);
        }

        if (!isNickValid(dto)) {
            errors.add(WRONG_NICK);
        }

        if (!errors.isEmpty()) {
            throw new WrongDtoException(errors);
        }
    }

    public boolean isPasswordValid(UserRequestDto dto) {
        return dto.getPassword() != null && PASSWORD_PATTERN.matcher(dto.getPassword()).matches();
    }

    public boolean isEmailValid(UserRequestDto dto) {
        return (dto.getEmail() != null && !dto.getEmail().isBlank())
                && VALID_EMAIL_ADDRESS_REGEX.matcher(dto.getEmail()).matches();
    }

    public boolean isNickValid(UserRequestDto dto) {
        return (dto.getUserNick() != null && !dto.getUserNick().isBlank()) &&
                (dto.getUserNick().length() <= NICK_MAX_LENGTH);
    }
}
