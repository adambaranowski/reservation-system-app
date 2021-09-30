package pl.adambaranowski.rsbackend.tests.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import pl.adambaranowski.rsbackend.model.Room;
import pl.adambaranowski.rsbackend.model.RoomStatus;
import pl.adambaranowski.rsbackend.model.dto.RoomRequestDto;
import pl.adambaranowski.rsbackend.model.dto.RoomResponseDto;
import pl.adambaranowski.rsbackend.repository.RoomRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.adambaranowski.rsbackend.security.UrlsConstants.ROOM_ENDPOINT;
import static pl.adambaranowski.rsbackend.tests.unit.TestUtils.*;
import static pl.adambaranowski.rsbackend.validator.ValidationConstants.*;

public class RoomApiTest extends BaseIntegrationTestClass {

    private static final String INVALID_ROOM_NUMBER = "Room of given number: %d does not exist";
    @Autowired
    RoomRepository roomRepository;

    @Test
    void getAllRooms_returnRoomList() throws Exception {
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room1 = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room room2 = Room.builder()
                .roomStatus(RoomStatus.ONLY_TEACHER)
                .number(TEST_ROOM_NUMBER + 1)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        clearRoomRepository();
        roomRepository.save(room1);
        roomRepository.save(room2);

        List<Room> expectedRooms = List.of(room1, room2);

        List<RoomResponseDto> expectedDtos = expectedRooms.stream()
                .map(roomResponseMapper::mapToDto)
                .collect(Collectors.toList());

        String body = mvc.perform(
                get(ROOM_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Room> roomsInDatabase = roomRepository.findAll();

        List<RoomResponseDto> actualDtos = Arrays.asList(mapper.readValue(body, RoomResponseDto[].class));

        assertEquals(expectedDtos, actualDtos);
        assertEquals(expectedRooms, roomsInDatabase);
    }

    @Test
    void getAllRooms_unauthorized_return403status() throws Exception {
        mvc.perform(
                get(ROOM_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void updateRoom_responseWithCorrectDto() throws Exception {
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        clearRoomRepository();
        int number = roomRepository.save(room).getNumber();

        RoomRequestDto updatedRoomDto = RoomRequestDto.builder()
                .roomStatus(RoomRequestDto.RoomStatusEnum.NORMAL)
                .roomNumber(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME + "updated")
                .equipmentItemsId(Collections.emptyList())
                .build();

        Room updatedRoom = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME + "updated")
                .reservations(null)
                .build();

        String body = mvc.perform(
                put(ROOM_ENDPOINT)
                        .content(mapper.writeValueAsString(updatedRoomDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RoomResponseDto expectedDto = roomResponseMapper.mapToDto(updatedRoom);

        Room roomInDatabase = roomRepository.findById(number).get();

        RoomResponseDto actualDto = mapper.readValue(body, RoomResponseDto.class);

        assertEquals(expectedDto, actualDto);
        assertEquals(updatedRoom, roomInDatabase);
    }

    @Test
    void updateRoom_emptyFields_return400Status() throws Exception {
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        clearRoomRepository();
        roomRepository.save(room);

        RoomRequestDto updatedRoomDto = RoomRequestDto.builder()
                .roomStatus(null)
                .roomNumber(null)
                .description(null)
                .equipmentItemsId(Collections.emptyList())
                .build();

        List<String> expectedErrorsList = List.of(
                NULL_ROOM_NUMBER,
                NULL_ROOM_STATUS,
                BLANK_DESCRIPTION
        );

        String body = mvc.perform(
                put(ROOM_ENDPOINT)
                        .content(mapper.writeValueAsString(updatedRoomDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> actualErrorsList = Arrays.asList(body.substring(1, body.length() - 1).split(", "));

        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void updateRoom_wrongNumberAndDescription_return400Status() throws Exception {
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        clearRoomRepository();
        roomRepository.save(room);

        RoomRequestDto updatedRoomDto = RoomRequestDto.builder()
                .roomStatus(RoomRequestDto.RoomStatusEnum.NORMAL)
                .roomNumber(TEST_WRONG_ROOM_NUMBER)
                .description(generateTestString(ROOM_DESCRIPTION_MAX_LENGTH + 1))
                .equipmentItemsId(Collections.emptyList())
                .build();

        List<String> expectedErrorsList = List.of(
                WRONG_ROOM_NUMBER,
                TOO_LONG_DESCRIPTION
        );

        String body = mvc.perform(
                put(ROOM_ENDPOINT)
                        .content(mapper.writeValueAsString(updatedRoomDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> actualErrorsList = Arrays.asList(body.substring(1, body.length() - 1).split(", "));

        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void updateRoom_unauthorized_return403Status() throws Exception {
        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        clearRoomRepository();
        roomRepository.save(room);

        RoomRequestDto updatedRoomDto = RoomRequestDto.builder()
                .roomStatus(RoomRequestDto.RoomStatusEnum.NORMAL)
                .roomNumber(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME + "updated")
                .equipmentItemsId(Collections.emptyList())
                .build();

        mvc.perform(
                put(ROOM_ENDPOINT)
                        .content(mapper.writeValueAsString(updatedRoomDto))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void updateRoom_notAllowed_return403Status() throws Exception {
        String[] studentTokenHeader = getStudentTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        clearRoomRepository();
        roomRepository.save(room);

        RoomRequestDto updatedRoomDto = RoomRequestDto.builder()
                .roomStatus(RoomRequestDto.RoomStatusEnum.NORMAL)
                .roomNumber(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME + "updated")
                .equipmentItemsId(Collections.emptyList())
                .build();

        mvc.perform(
                put(ROOM_ENDPOINT)
                        .content(mapper.writeValueAsString(updatedRoomDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(studentTokenHeader[AUTH_HEADER_NAME], studentTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void addRoom_responseWithCorrectDto() throws Exception {
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .equipmentItems(Collections.emptySet())
                .reservations(null)
                .build();

        clearRoomRepository();

        RoomRequestDto roomRequestDto = RoomRequestDto.builder()
                .roomStatus(RoomRequestDto.RoomStatusEnum.NORMAL)
                .roomNumber(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .equipmentItemsId(Collections.emptyList())
                .build();

        String body = mvc.perform(
                post(ROOM_ENDPOINT)
                        .content(mapper.writeValueAsString(roomRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RoomResponseDto expectedDto = roomResponseMapper.mapToDto(room);

        Room roomInDatabase = roomRepository.findById(TEST_ROOM_NUMBER).get();

        RoomResponseDto actualDto = mapper.readValue(body, RoomResponseDto.class);

        assertEquals(expectedDto, actualDto);
        assertEquals(room, roomInDatabase);
    }

    @Test
    void addRoom_emptyFields_return400Status() throws Exception {
        String[] adminTokenHeader = getAdminTokenHeader();

        clearRoomRepository();

        RoomRequestDto roomRequestDto = RoomRequestDto.builder()
                .roomStatus(null)
                .roomNumber(null)
                .description(null)
                .equipmentItemsId(Collections.emptyList())
                .build();

        List<String> expectedErrorsList = List.of(
                NULL_ROOM_NUMBER,
                NULL_ROOM_STATUS,
                BLANK_DESCRIPTION
        );

        String body = mvc.perform(
                post(ROOM_ENDPOINT)
                        .content(mapper.writeValueAsString(roomRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> actualErrorsList = Arrays.asList(body.substring(1, body.length() - 1).split(", "));

        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void addRoom_wrongNumberAndDescription_return400Status() throws Exception {
        String[] adminTokenHeader = getAdminTokenHeader();

        clearRoomRepository();

        RoomRequestDto roomRequestDto = RoomRequestDto.builder()
                .roomStatus(RoomRequestDto.RoomStatusEnum.NORMAL)
                .roomNumber(TEST_WRONG_ROOM_NUMBER)
                .description(generateTestString(ROOM_DESCRIPTION_MAX_LENGTH + 1))
                .equipmentItemsId(Collections.emptyList())
                .build();

        List<String> expectedErrorsList = List.of(
                WRONG_ROOM_NUMBER,
                TOO_LONG_DESCRIPTION
        );

        String body = mvc.perform(
                post(ROOM_ENDPOINT)
                        .content(mapper.writeValueAsString(roomRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> actualErrorsList = Arrays.asList(body.substring(1, body.length() - 1).split(", "));

        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void addRoom_unauthorized_return403Status() throws Exception {
        clearRoomRepository();

        RoomRequestDto roomRequestDto = RoomRequestDto.builder()
                .roomStatus(RoomRequestDto.RoomStatusEnum.NORMAL)
                .roomNumber(TEST_WRONG_ROOM_NUMBER)
                .description(generateTestString(ROOM_DESCRIPTION_MAX_LENGTH + 1))
                .equipmentItemsId(Collections.emptyList())
                .build();

        mvc.perform(
                post(ROOM_ENDPOINT)
                        .content(mapper.writeValueAsString(roomRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void addRoom_notAllowed_return403Status() throws Exception {
        String[] studentTokenHeader = getStudentTokenHeader();

        clearRoomRepository();

        RoomRequestDto roomRequestDto = RoomRequestDto.builder()
                .roomStatus(RoomRequestDto.RoomStatusEnum.NORMAL)
                .roomNumber(TEST_WRONG_ROOM_NUMBER)
                .description(generateTestString(ROOM_DESCRIPTION_MAX_LENGTH + 1))
                .equipmentItemsId(Collections.emptyList())
                .build();

        mvc.perform(
                post(ROOM_ENDPOINT)
                        .content(mapper.writeValueAsString(roomRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(studentTokenHeader[AUTH_HEADER_NAME], studentTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void getRoomById_responseWithCorrectDto() throws Exception {
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        clearRoomRepository();
        Room attachedRoom = roomRepository.save(room);

        String body = mvc.perform(
                get(ROOM_ENDPOINT + "/" + TEST_ROOM_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RoomResponseDto expectedDto = roomResponseMapper.mapToDto(attachedRoom);

        Room roomInDatabase = roomRepository.findById(TEST_ROOM_NUMBER).get();

        RoomResponseDto actualDto = mapper.readValue(body, RoomResponseDto.class);

        assertEquals(expectedDto, actualDto);
        assertEquals(attachedRoom, roomInDatabase);
    }

    @Test
    void getRoomById_invalidId_return404Status() throws Exception {
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        clearRoomRepository();
        roomRepository.save(room);

        int wrongNumber = TEST_ROOM_NUMBER + 1;

        String actualResponse = mvc.perform(
                get(ROOM_ENDPOINT + "/" + wrongNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = String.format(INVALID_ROOM_NUMBER, wrongNumber);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getRoomById_unauthorized_return403Status() throws Exception {
        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        clearRoomRepository();
        roomRepository.save(room);

        mvc.perform(
                get(ROOM_ENDPOINT + "/" + TEST_ROOM_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void deleteRoomById_successResponse() throws Exception {
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        clearRoomRepository();
        roomRepository.save(room);

        mvc.perform(
                delete(ROOM_ENDPOINT + "/" + TEST_ROOM_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Optional<Room> roomInDatabase = roomRepository.findById(TEST_ROOM_NUMBER);

        assertTrue(roomInDatabase.isEmpty());
    }

    @Test
    void deleteRoomById_invalidId_return404Status() throws Exception {
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        clearRoomRepository();
        roomRepository.save(room);

        int wrongNumber = TEST_ROOM_NUMBER + 1;

        mvc.perform(
                delete(ROOM_ENDPOINT + "/" + wrongNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();
    }

    @Test
    void deleteRoomById_notAllowed_return403Status() throws Exception {
        String[] studentTokenHeader = getStudentTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        clearRoomRepository();
        roomRepository.save(room);

        mvc.perform(
                delete(ROOM_ENDPOINT + "/" + TEST_ROOM_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(studentTokenHeader[AUTH_HEADER_NAME], studentTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void deleteRoomById_unauthorized_return403Status() throws Exception {
        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        clearRoomRepository();
        roomRepository.save(room);

        mvc.perform(
                delete(ROOM_ENDPOINT + "/" + TEST_ROOM_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }


    private void clearRoomRepository() {
        roomRepository.findAll().forEach(r -> {
            r.getEquipmentItems().forEach(e -> e.setRoom(null));
            roomRepository.delete(r);
        });
    }
}
