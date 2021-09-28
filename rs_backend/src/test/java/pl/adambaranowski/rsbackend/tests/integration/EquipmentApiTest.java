package pl.adambaranowski.rsbackend.tests.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import pl.adambaranowski.rsbackend.model.Equipment;
import pl.adambaranowski.rsbackend.model.Room;
import pl.adambaranowski.rsbackend.model.RoomStatus;
import pl.adambaranowski.rsbackend.model.dto.EquipmentRequestDto;
import pl.adambaranowski.rsbackend.model.dto.EquipmentResponseDto;
import pl.adambaranowski.rsbackend.repository.EquipmentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.adambaranowski.rsbackend.security.UrlsConstants.EQUIPMENT_ENDPOINT;
import static pl.adambaranowski.rsbackend.tests.unit.TestUtils.*;
import static pl.adambaranowski.rsbackend.validator.ValidationConstants.*;

public class EquipmentApiTest extends BaseIntegrationTestClass{

    @Autowired
    EquipmentRepository equipmentRepository;

    private static final String INVALID_EQUIPMENT_ID = "Equipment of given Id does not exist";

    @Test
    void createEquipment_responseWithCorrectDto() throws Exception {
        String[] adminTokenHeader = getAdminTokenHeader();

        EquipmentRequestDto requestDto = EquipmentRequestDto.builder()
                .name("Equipment")
                .description("Test equipment")
                .roomNumber(22)
                .build();

        String body = mvc.perform(
                post(EQUIPMENT_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto))
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        EquipmentResponseDto responseDto = mapper.readValue(body, EquipmentResponseDto.class);

        assertEquals("Equipment", responseDto.getName());
        assertEquals("Test equipment", responseDto.getDescription());
        assertEquals(22, responseDto.getRoomNumber());

        Equipment equipmentInDataBase = equipmentRepository.findById(responseDto.getId()).get();
        assertEquals("Equipment", equipmentInDataBase.getName());
        assertEquals("Test equipment", equipmentInDataBase.getDescription());
        assertEquals(22, equipmentInDataBase.getRoom().getNumber());
    }

    @Test
    void createEquipment_responseWithWrongDto() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        EquipmentRequestDto requestDto = EquipmentRequestDto.builder()
                .name(null)
                .description(null)
                .roomNumber(null)
                .build();

        List<String> expectedErrorsList = List.of(
                EMPTY_NAME,
                EMPTY_DESCRIPTION,
                NULL_ROOM_NUMBER
        );

        String body = mvc.perform(
                post(EQUIPMENT_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
                        .content(mapper.writeValueAsString(requestDto))
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
    void createEquipment_responseWithWrongDto_tooLongDescription() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        EquipmentRequestDto requestDto = EquipmentRequestDto.builder()
                .name(null)
                .description(generateTestString(DESCRIPTION_MAX_LENGTH + 1))
                .roomNumber(null)
                .build();

        List<String> expectedErrorsList = List.of(
                EMPTY_NAME,
                TOO_LONG_DESCRIPTION,
                NULL_ROOM_NUMBER
        );

        String body = mvc.perform(
                post(EQUIPMENT_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
                        .content(mapper.writeValueAsString(requestDto))
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
    void getEquipment_returnAllEquipmentItems() throws Exception {
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        Equipment equipment1 = Equipment.builder()
                .name("eq1")
                .description("des1")
                .room(attachedRoom)
                .build();

        Equipment equipment2 = Equipment.builder()
                .name("eq2")
                .description("des2")
                .room(attachedRoom)
                .build();

        equipmentRepository.deleteAll();

        equipmentRepository.save(equipment1);
        equipmentRepository.save(equipment2);

        String body = mvc.perform(
                get(EQUIPMENT_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(body);

        List<EquipmentResponseDto> actualDtos = Arrays.asList(mapper.readValue(body, EquipmentResponseDto[].class));

        List<EquipmentResponseDto> expectedDtos = List.of(equipmentResponseMapper.mapToDto(equipment1),
                equipmentResponseMapper.mapToDto(equipment2));

        assertEquals(expectedDtos, actualDtos);
    }

    @Test
    void getEquipmentById_responseWithCorrectDto() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        Equipment equipment = Equipment.builder()
                .name("eq")
                .description("des")
                .room(attachedRoom)
                .build();

        equipmentRepository.deleteAll();

        int id = equipmentRepository.save(equipment).getId();

        String body = mvc.perform(
                get(EQUIPMENT_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        EquipmentResponseDto actualDto = mapper.readValue(body, EquipmentResponseDto.class);
        EquipmentResponseDto expectedDto = equipmentResponseMapper.mapToDto(equipment);

        Equipment actualEquipment = equipmentRepository.findById(id).get();

        assertEquals(expectedDto, actualDto);
        assertEquals(equipment, actualEquipment);
    }

    @Test
    void getEquipmentById_responseWithWrongDto() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        Equipment equipment = Equipment.builder()
                .name("eq")
                .description("des")
                .room(attachedRoom)
                .build();

        equipmentRepository.deleteAll();

        int id = equipmentRepository.save(equipment).getId();

        int wrongId = id + 1;

        String actualResponse = mvc.perform(
                get(EQUIPMENT_ENDPOINT + "/" + wrongId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Optional<Equipment> actualEquipment = equipmentRepository.findById(wrongId);

        assertEquals(INVALID_EQUIPMENT_ID, actualResponse);
        assertTrue(actualEquipment.isEmpty());
    }

    @Test
    void updateEquipmentById_responseWithCorrectDto() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        Equipment equipment = Equipment.builder()
                .name("eq")
                .description("des")
                .room(attachedRoom)
                .build();

        equipmentRepository.deleteAll();

        int id = equipmentRepository.save(equipment).getId();

        Equipment updatedEquipment = Equipment.builder()
                .id(id)
                .name("updatedEq")
                .description("updatedDes")
                .room(attachedRoom)
                .build();

        EquipmentRequestDto updatedEquipmentDto = EquipmentRequestDto.builder()
                .name("updatedEq")
                .description("updatedDes")
                .roomNumber(TEST_ROOM_NUMBER)
                .build();

        String body = mvc.perform(
                put(EQUIPMENT_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedEquipmentDto))
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        EquipmentResponseDto actualDto = mapper.readValue(body, EquipmentResponseDto.class);

        EquipmentResponseDto expectedDto = EquipmentResponseDto.builder()
                .id(id)
                .name("updatedEq")
                .description("updatedDes")
                .roomNumber(TEST_ROOM_NUMBER)
                .build();

        Equipment actualEquipment = equipmentRepository.findById(id).get();

        assertEquals(expectedDto, actualDto);
        assertEquals(updatedEquipment, actualEquipment);
    }

    @Test
    void updateEquipmentById_responseWithWrongDto() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        Equipment equipment = Equipment.builder()
                .name("eq")
                .description("des")
                .room(attachedRoom)
                .build();

        equipmentRepository.deleteAll();

        int id = equipmentRepository.save(equipment).getId();

        EquipmentRequestDto updatedEquipmentDto = EquipmentRequestDto.builder()
                .name(null)
                .description(null)
                .roomNumber(null)
                .build();

        List<String> expectedErrorsList = List.of(
                EMPTY_NAME,
                EMPTY_DESCRIPTION,
                NULL_ROOM_NUMBER
        );

        String body = mvc.perform(
                put(EQUIPMENT_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedEquipmentDto))
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> actualErrorsList = Arrays.asList(body.substring(1, body.length() - 1).split(", "));

        equipment.setId(id);
        Equipment actualEquipment = equipmentRepository.findById(id).get();

        assertEquals(equipment, actualEquipment);
        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void updateEquipmentById_responseWithWrongDto_tooLongDescription() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        Equipment equipment = Equipment.builder()
                .name("eq")
                .description("des")
                .room(attachedRoom)
                .build();

        equipmentRepository.deleteAll();

        int id = equipmentRepository.save(equipment).getId();

        EquipmentRequestDto updatedEquipmentDto = EquipmentRequestDto.builder()
                .name(null)
                .description(generateTestString(DESCRIPTION_MAX_LENGTH + 1))
                .roomNumber(null)
                .build();

        List<String> expectedErrorsList = List.of(
                EMPTY_NAME,
                TOO_LONG_DESCRIPTION,
                NULL_ROOM_NUMBER
        );

        String body = mvc.perform(
                put(EQUIPMENT_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedEquipmentDto))
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> actualErrorsList = Arrays.asList(body.substring(1, body.length() - 1).split(", "));

        equipment.setId(id);
        Equipment actualEquipment = equipmentRepository.findById(id).get();

        assertEquals(equipment, actualEquipment);
        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void updateEquipmentById_responseWithWrongDto_wrongId() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        Equipment equipment = Equipment.builder()
                .name("eq")
                .description("des")
                .room(attachedRoom)
                .build();

        equipmentRepository.deleteAll();

        int id = equipmentRepository.save(equipment).getId();
        int wrongId = id + 1;

        EquipmentRequestDto updatedEquipmentDto = EquipmentRequestDto.builder()
                .name("updatedEq")
                .description("updatedDes")
                .roomNumber(TEST_ROOM_NUMBER)
                .build();

        String actualResponse = mvc.perform(
                put(EQUIPMENT_ENDPOINT + "/" + wrongId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedEquipmentDto))
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        equipment.setId(id);
        Equipment actualEquipment = equipmentRepository.findById(id).get();

        assertEquals(equipment, actualEquipment);
        assertEquals(INVALID_EQUIPMENT_ID, actualResponse);
    }

    @Test
    void deleteEquipmentById_successResponse() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        Equipment equipment = Equipment.builder()
                .name("eq")
                .description("des")
                .room(attachedRoom)
                .build();

        equipmentRepository.deleteAll();

        int id = equipmentRepository.save(equipment).getId();

        assertTrue(equipmentRepository.findById(id).isPresent());

        mvc.perform(
                delete(EQUIPMENT_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(equipmentRepository.findById(id).isEmpty());
        //blad
    }

    @Test
    void deleteEquipmentById_notFoundResponse() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        Equipment equipment = Equipment.builder()
                .name("eq")
                .description("des")
                .room(attachedRoom)
                .build();

        equipmentRepository.deleteAll();

        int id = equipmentRepository.save(equipment).getId();
        int wrongId = id + 1;

        assertTrue(equipmentRepository.findById(id).isPresent());

        String actualResponse = mvc.perform(
                delete(EQUIPMENT_ENDPOINT + "/" + wrongId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(INVALID_EQUIPMENT_ID, actualResponse);
    }
}
