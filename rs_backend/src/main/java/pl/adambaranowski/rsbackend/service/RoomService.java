package pl.adambaranowski.rsbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.adambaranowski.rsbackend.exception.ConflictException;
import pl.adambaranowski.rsbackend.model.Equipment;
import pl.adambaranowski.rsbackend.model.Room;
import pl.adambaranowski.rsbackend.model.RoomStatus;
import pl.adambaranowski.rsbackend.model.dto.RoomRequestDto;
import pl.adambaranowski.rsbackend.model.dto.RoomResponseDto;
import pl.adambaranowski.rsbackend.repository.EquipmentRepository;
import pl.adambaranowski.rsbackend.repository.RoomRepository;
import pl.adambaranowski.rsbackend.service.utils.RoomResponseMapper;
import pl.adambaranowski.rsbackend.validator.RoomDtoValidator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private static final String NO_SUCH_ROOM_MESSAGE = "Room of given number: %d does not exist";
    private static final String ROOM_CONFLICT_MESSAGE = "Room of given number: %d has been already created";
    private static final String NO_SUCH_EQUIPMENT_MESSAGE = "Equipment of given id: %d does not exist";

    private final RoomRepository roomRepository;
    private final EquipmentRepository equipmentRepository;
    private final RoomResponseMapper responseMapper;
    private final RoomDtoValidator validator;

    public List<RoomResponseDto> getAll() {
        roomRepository.findAll().forEach(System.out::println);
        return roomRepository.findAll()
                .stream().map(responseMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public RoomResponseDto getRoomById(Integer roomNumber) {
        Optional<Room> room = roomRepository.findById(roomNumber);
        return responseMapper.mapToDto(room.orElseThrow(() -> new NoSuchElementException(String.format(NO_SUCH_ROOM_MESSAGE, roomNumber))));
    }

    public void deleteRoomById(Integer roomNumber) {
        Room room = roomRepository.findById(roomNumber)
                .orElseThrow(() -> new NoSuchElementException(String.format(NO_SUCH_ROOM_MESSAGE, roomNumber)));

        room.getEquipmentItems().forEach(equipment -> equipment.setRoom(null));

        roomRepository.delete(room);
    }

    public RoomResponseDto postNewRoom(RoomRequestDto roomRequestDto) {

        validator.validate(roomRequestDto);

        List<Equipment> equipmentItemsForRoom = getEquipmentItemsForRoomDto(roomRequestDto);

        int roomNumber = roomRequestDto.getRoomNumber();

        if (roomRepository.existsById(roomNumber)) {
            throw new ConflictException(String.format(ROOM_CONFLICT_MESSAGE, roomNumber));
        }

        Room room = new Room();
        room.setNumber(roomNumber);
        room.setDescription(roomRequestDto.getDescription());
        room.setRoomStatus(RoomStatus.valueOf(roomRequestDto.getRoomStatus().getValue()));
        equipmentItemsForRoom.forEach(room::addEquipment);

        roomRepository.save(room);
        return responseMapper.mapToDto(room);
    }


    public RoomResponseDto updateRoom(RoomRequestDto roomRequestDto) {
        validator.validate(roomRequestDto);

        List<Equipment> equipmentItemsForRoom = getEquipmentItemsForRoomDto(roomRequestDto);

        int roomNumber = roomRequestDto.getRoomNumber();

        Room room = roomRepository.findById(roomRequestDto.getRoomNumber())
                .orElseThrow(() -> new NoSuchElementException(String.format(NO_SUCH_ROOM_MESSAGE, roomNumber)));

        room.setDescription(roomRequestDto.getDescription());
        room.setRoomStatus(RoomStatus.valueOf(roomRequestDto.getRoomStatus().getValue()));

        // delete existing items
        room.getEquipmentItems().forEach(equipment -> equipment.setRoom(null));
        room.getEquipmentItems().clear();

        // add new items
        equipmentItemsForRoom.forEach(room::addEquipment);

        roomRepository.save(room);
        return responseMapper.mapToDto(room);
    }

    private List<Equipment> getEquipmentItemsForRoomDto(RoomRequestDto dto) {
        return dto.getEquipmentItemsId().stream()
                .map(equipmentId -> equipmentRepository.findById(equipmentId)
                        .orElseThrow(() -> new NoSuchElementException(String.format(NO_SUCH_EQUIPMENT_MESSAGE, equipmentId))))
                .collect(Collectors.toUnmodifiableList());
    }
}
