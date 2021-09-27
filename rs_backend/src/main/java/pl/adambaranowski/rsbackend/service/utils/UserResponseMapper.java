package pl.adambaranowski.rsbackend.service.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import pl.adambaranowski.rsbackend.model.Authority;
import pl.adambaranowski.rsbackend.model.User;
import pl.adambaranowski.rsbackend.model.dto.UserResponseDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {

    @Mappings(
            @Mapping(source = "authorities", target = "authorities", qualifiedByName = "mapAuthorities")
    )
    UserResponseDto mapToDto(User user);

    @Named("mapAuthorities")
    default List<String> mapAuthorities(Set<Authority> authorities) {
        return authorities.stream()
                .map(Authority::getRole)
                .collect(Collectors.toList());
    }
}
