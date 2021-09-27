package pl.adambaranowski.rsbackend.validator;

import org.springframework.stereotype.Component;
import pl.adambaranowski.rsbackend.exception.WrongDtoException;
import pl.adambaranowski.rsbackend.model.dto.EquipmentRequestDto;

import java.util.ArrayList;
import java.util.List;

import static pl.adambaranowski.rsbackend.validator.ValidationConstants.*;

@Component
public class EquipmentDtoValidator {

    public void validate(EquipmentRequestDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getName() == null || dto.getName().isBlank()) {
            errors.add(EMPTY_NAME);
        } else {
            if (dto.getName().length() > NAME_MAX_LENGTH) {
                errors.add(TOO_LONG_NAME);
            }
        }

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            errors.add(EMPTY_DESCRIPTION);
        } else {
            if (dto.getDescription().length() > DESCRIPTION_MAX_LENGTH) {
                errors.add(TOO_LONG_DESCRIPTION);
            }
        }

        if (dto.getRoomNumber() == null) {
            errors.add(NULL_ROOM_NUMBER);
        }

        if (!errors.isEmpty()) {
            throw new WrongDtoException(errors);
        }
    }
}
