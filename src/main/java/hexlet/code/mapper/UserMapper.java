package hexlet.code.mapper;

import hexlet.code.model.User;
import hexlet.code.model.dto.User.UserCreateDTO;
import hexlet.code.model.dto.User.UserDTO;
import hexlet.code.model.dto.User.UserUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Маппер для преобразования между сущностью {@link User} и её DTO ({@link UserDTO}, {@link UserCreateDTO},
 * {@link UserUpdateDTO}).
 * Использует MapStruct для генерации реализации.
 * Использует вспомогательные мапперы {@link JsonNullableMapper} и {@link ReferenceMapper}.
 */
@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    /**
     * Преобразует сущность {@link User} в DTO {@link UserDTO}.
     * Используется при возврате информации о пользователе.
     *
     * @param model сущность пользователя
     * @return DTO {@link UserDTO} с данными пользователя
     */
    public abstract UserDTO map(User model);

    /**
     * Преобразует DTO {@link UserCreateDTO} в сущность {@link User}.
     * Используется при создании нового пользователя.
     * Пароль из DTO будет преобразован и сохранён в сущности.
     *
     * @param dto DTO с данными для создания пользователя
     * @return новая сущность {@link User}
     */
    public abstract User map(UserCreateDTO dto);

    /**
     * Обновляет существующую сущность {@link User} на основе данных из DTO {@link UserUpdateDTO}.
     * Игнорирует поле 'password' в DTO при обновлении сущности.
     * Используется при обновлении данных пользователя.
     *
     * @param dto   DTO с новыми данными пользователя
     * @param model сущность пользователя для обновления
     */
    @Mapping(target = "password", ignore = true)
    public abstract void update(UserUpdateDTO dto, @MappingTarget User model);
}
