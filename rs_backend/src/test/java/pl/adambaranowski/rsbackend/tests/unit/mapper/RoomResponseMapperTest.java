package pl.adambaranowski.rsbackend.tests.unit.mapper;

import org.junit.jupiter.api.Test;
import pl.adambaranowski.rsbackend.model.Room;
import pl.adambaranowski.rsbackend.model.RoomStatus;
import pl.adambaranowski.rsbackend.model.dto.RoomResponseDto;
import pl.adambaranowski.rsbackend.service.utils.RoomResponseMapper;
import pl.adambaranowski.rsbackend.service.utils.RoomResponseMapperImpl;

import java.util.Collections;

import static pl.adambaranowski.rsbackend.tests.unit.TestUtils.*;
import static pl.adambaranowski.rsbackend.validator.ValidationConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class RoomResponseMapperTest {
    RoomResponseMapper mapper = new RoomResponseMapperImpl();

    @Test
    void mapNullRoom(){
        mapper.mapToDto(null);
    }

    @Test
    void mapRoom_nullFields(){
        Room room = new Room();
        room.setDescription(null);
        room.setNumber(null);
        room.setRoomStatus(null);
        room.setEquipmentItems(Collections.emptySet());
        room.setReservations(Collections.emptySet());

        mapper.mapToDto(room);
    }

    @Test
    void mapRoom_nonEmptyFields(){
        Room room = new Room();
        String description = generateTestString(DESCRIPTION_MAX_LENGTH);
        room.setDescription(description);
        room.setNumber(TEST_ROOM_NUMBER);
        room.setRoomStatus(RoomStatus.NORMAL);
        room.setEquipmentItems(Collections.emptySet());
        room.setReservations(Collections.emptySet());

        RoomResponseDto expectedResponseDto = new RoomResponseDto();
        expectedResponseDto.setDescription(description);
        expectedResponseDto.setRoomNumber(TEST_ROOM_NUMBER);
        expectedResponseDto.setRoomStatus(RoomResponseDto.RoomStatusEnum.NORMAL);
        expectedResponseDto.setEquipmentItems(Collections.emptyList());

        RoomResponseDto actualResponseDto = mapper.mapToDto(room);

        assertEquals(expectedResponseDto, actualResponseDto);
    }

}
