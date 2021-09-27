package pl.adambaranowski.rsbackend.tests.unit.mapper;

import org.junit.jupiter.api.Test;
import pl.adambaranowski.rsbackend.model.Equipment;
import pl.adambaranowski.rsbackend.model.Room;
import pl.adambaranowski.rsbackend.model.dto.EquipmentResponseDto;
import pl.adambaranowski.rsbackend.service.utils.EquipmentResponseMapper;
import pl.adambaranowski.rsbackend.service.utils.EquipmentResponseMapperImpl;

import static pl.adambaranowski.rsbackend.tests.unit.TestUtils.*;
import static pl.adambaranowski.rsbackend.validator.ValidationConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class EquipmentResponseMapperTest {
    EquipmentResponseMapper mapper = new EquipmentResponseMapperImpl();

    @Test
    void mapNullItem(){
        mapper.mapToDto(null);
    }

    @Test
    void mapItem_nullFields(){
        Equipment item = new Equipment();
        item.setDescription(null);
        item.setRoom(null);
        item.setId(null);
        item.setName(null);

        mapper.mapToDto(item);
    }

    @Test
    void mapItem_nonEmptyFields(){
        Equipment item = new Equipment();
        String description = generateTestString(DESCRIPTION_MAX_LENGTH);
        Room testRoom = new Room();
        testRoom.setNumber(TEST_ROOM_NUMBER);

        item.setDescription(description);
        item.setRoom(testRoom);
        item.setId(TEST_ID);
        item.setName(TEST_NAME);

        EquipmentResponseDto expectedResponseDto = new EquipmentResponseDto();
        expectedResponseDto.setDescription(description);
        expectedResponseDto.setRoomNumber(TEST_ROOM_NUMBER);
        expectedResponseDto.setId(TEST_ID);
        expectedResponseDto.setName(TEST_NAME);

        EquipmentResponseDto actualResponseDto = mapper.mapToDto(item);

        assertEquals(expectedResponseDto, actualResponseDto);
    }
}