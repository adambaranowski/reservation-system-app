package pl.adambaranowski.rsbackend.service.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.adambaranowski.rsbackend.model.Equipment;
import pl.adambaranowski.rsbackend.model.dto.EquipmentResponseDto;

@Mapper(componentModel = "spring")
public interface EquipmentResponseMapper {

    @Mappings({
            @Mapping(target = "roomNumber", source = "room.number")
    })
    EquipmentResponseDto mapToDto(Equipment item);

}
