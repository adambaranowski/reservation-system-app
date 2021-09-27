package pl.adambaranowski.rsbackend.validator;


import org.springframework.stereotype.Component;
import pl.adambaranowski.rsbackend.exception.WrongDtoException;
import pl.adambaranowski.rsbackend.model.dto.RoomRequestDto;

import java.util.ArrayList;
import java.util.List;

import static pl.adambaranowski.rsbackend.validator.ValidationConstants.*;

@Component
public class RoomDtoValidator {

    public void validate(RoomRequestDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getRoomNumber() == null) {
            errors.add(NULL_ROOM_NUMBER);
        } else {
            if (dto.getRoomNumber() < 0 || dto.getRoomNumber() > 10_000) {
                errors.add(WRONG_ROOM_NUMBER);
            }
        }

        if (dto.getRoomStatus() == null) {
            errors.add(NULL_ROOM_STATUS);
        }

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            errors.add(BLANK_DESCRIPTION);
        } else {
            if (dto.getDescription().length() > ROOM_DESCRIPTION_MAX_LENGTH) {
                errors.add(TOO_LONG_DESCRIPTION);
            }
        }

        if (!errors.isEmpty()) {
            throw new WrongDtoException(errors);
        }
    }
}
