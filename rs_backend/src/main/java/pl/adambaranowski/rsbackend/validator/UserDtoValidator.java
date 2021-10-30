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
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    public static final String PASSWORD_INSTRUCTIONS = "\n" +
            "a digit must occur at least once\n" +
            "a lower case letter must occur at least once\n" +
            "an upper case letter must occur at least once\n" +
            "a special character must occur at least once\n" +
            "no whitespace allowed in the entire string\n" +
            "anything, at least eight places though\n";


    public void validateUserDto(UserRequestDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getPassword() == null || !PASSWORD_PATTERN.matcher(dto.getPassword()).matches()) {
            errors.add(PASSWORD_INSTRUCTIONS);
        }

        System.out.println(dto.getEmail());
        System.out.println(VALID_EMAIL_ADDRESS_REGEX.matcher(dto.getEmail()).matches());

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            errors.add(EMPTY_EMAIL);
        } else {
            if (!VALID_EMAIL_ADDRESS_REGEX.matcher(dto.getEmail()).matches()) {
                errors.add(WRONG_EMAIL_PATTERN);
            }
        }

        if (dto.getAuthorities() == null || dto.getAuthorities().isEmpty()) {
            errors.add(NO_AUTHORITY);
        }

        if (dto.getUserNick() == null || dto.getUserNick().isBlank()) {
            errors.add(EMPTY_NICK);
        } else {
            if (dto.getUserNick().length() > NICK_MAX_LENGTH) {
                errors.add(TOO_LONG_NICK);
            }
        }

        if (!errors.isEmpty()) {
            throw new WrongDtoException(errors);
        }
    }
}
