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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.adambaranowski.rsbackend.security.UrlsConstants.EQUIPMENT_ENDPOINT;
import static pl.adambaranowski.rsbackend.tests.unit.TestUtils.*;

public class EquipmentApiTest extends BaseIntegrationTestClass{

    @Autowired
    EquipmentRepository equipmentRepository;

    @Test
    void addNewEquipmentEndpoint_createEquipment_responseWithCorrectDto() throws Exception {
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
    void getEquipmentById_returnEquipmentItem() throws Exception{
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

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void updateEquipmentById_returnEquipmentItem() throws Exception{
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

        EquipmentRequestDto updatedEquipment = EquipmentRequestDto.builder()
                .name("updatedEq")
                .description("updatedDes")
                .roomNumber(TEST_ROOM_NUMBER)
                .build();

        mvc.perform(
                put(EQUIPMENT_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedEquipment))
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        String body = mvc.perform(
                get(EQUIPMENT_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(equipment))
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

        assertEquals(expectedDto, actualDto);
    }
}
