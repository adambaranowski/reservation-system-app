package pl.adambaranowski.rsbackend.service.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import pl.adambaranowski.rsbackend.model.Equipment;
import pl.adambaranowski.rsbackend.model.Room;
import pl.adambaranowski.rsbackend.model.dto.EquipmentResponseDto;
import pl.adambaranowski.rsbackend.model.dto.RoomResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoomResponseMapper {

    @Mappings({
            @Mapping(source = "number", target = "roomNumber"),
            @Mapping(source = "equipmentItems", target = "equipmentItems", qualifiedByName = "itemsMapping")
    })
    RoomResponseDto mapToDto(Room room);

    @Named("itemsMapping")
    default List<EquipmentResponseDto> equipmentItemsToEquipmentResponseDto(Set<Equipment> equipmentItems) {
        List<EquipmentResponseDto> equipmentResponseDtos = new ArrayList<>();

        for (Equipment item : equipmentItems
        ) {
            EquipmentResponseDto dto = new EquipmentResponseDto();
            dto.setDescription(item.getDescription());
            dto.setId(item.getId());
            dto.setRoomNumber(item.getRoom().getNumber());
            dto.setName(item.getName());
            equipmentResponseDtos.add(dto);
        }

        return equipmentResponseDtos;
    }
}
