package hexlet.code.mapper;

import hexlet.code.model.User;
import hexlet.code.model.dto.UserDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;


@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @IterableMapping(qualifiedByName = "skipReferenceMapper")
    public abstract UserDTO map(User model);

    @Mapping(target = "id", ignore = true)
    public abstract User map(UserDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Чтобы не затереть пароль в сервисе!!
    public abstract void update(UserDTO dto, @MappingTarget User model);
}
