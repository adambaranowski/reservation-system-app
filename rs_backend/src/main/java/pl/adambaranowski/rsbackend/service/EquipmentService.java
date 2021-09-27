package pl.adambaranowski.rsbackend.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.adambaranowski.rsbackend.model.Equipment;
import pl.adambaranowski.rsbackend.model.Room;
import pl.adambaranowski.rsbackend.model.dto.EquipmentRequestDto;
import pl.adambaranowski.rsbackend.model.dto.EquipmentResponseDto;
import pl.adambaranowski.rsbackend.repository.EquipmentRepository;
import pl.adambaranowski.rsbackend.repository.RoomRepository;
import pl.adambaranowski.rsbackend.service.utils.EquipmentResponseMapper;
import pl.adambaranowski.rsbackend.validator.EquipmentDtoValidator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentService.class);

    private final EquipmentRepository equipmentRepository;
    private final RoomRepository roomRepository;
    private final EquipmentResponseMapper responseMapper;
    private final EquipmentDtoValidator validator;

    public List<EquipmentResponseDto> getAll() {
        return equipmentRepository.findAll().stream()
                .map(responseMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public EquipmentResponseDto addNewEquipmentItem(EquipmentRequestDto equipmentRequestDto) {
        validator.validate(equipmentRequestDto);

        Equipment newEquipmentEntity = new Equipment();

        Room room = roomRepository.findById(equipmentRequestDto.getRoomNumber().intValue()).orElseThrow(() -> new NoSuchElementException("Room not found"));
        room.addEquipment(newEquipmentEntity);

        newEquipmentEntity.setDescription(equipmentRequestDto.getDescription());
        newEquipmentEntity.setName(equipmentRequestDto.getName());

        equipmentRepository.save(newEquipmentEntity);

        LOGGER.info("ADDED NEW EQUIPMENT");

        return responseMapper.mapToDto(newEquipmentEntity);
    }

    public EquipmentResponseDto getEquipmentItemById(Integer equipmentId) {
        Optional<Equipment> equipment = equipmentRepository.findById(equipmentId);

        if (equipment.isPresent()) {
            return responseMapper.mapToDto(equipment.get());
        } else {
            throw new NoSuchElementException("Equipment of given Id does not exist");
        }
    }

    @Transactional
    public void deleteEquipmentById(Integer equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NoSuchElementException("Equipment Of given Id does not exist"));

        equipmentRepository.delete(equipment);
    }

    @Transactional
    public EquipmentResponseDto updateEquipmentItemById(Integer equipmentId, EquipmentRequestDto equipmentRequestDto) {
        validator.validate(equipmentRequestDto);

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NoSuchElementException("Equipment Of given Id does not exist"));

        // Set new room number
        if (equipmentRequestDto.getRoomNumber() != null) {
            Room room = roomRepository.findById(equipmentRequestDto.getRoomNumber().intValue())
                    .orElseThrow(() -> new NoSuchElementException("Room of given number does not exist"));
            room.addEquipment(equipment);
        }

        // Set new name if request name not null or blank
        if (equipmentRequestDto.getName() != null && !equipmentRequestDto.getName().isBlank()) {
            equipment.setName(equipmentRequestDto.getName());
        }

        // Set Description
        equipment.setDescription(equipmentRequestDto.getDescription());

        // Return new state of equipment item
        return responseMapper.mapToDto(equipment);
    }
}
