package hexlet.code.mapper;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Маппер для преобразования между объектами JsonNullable и обычными объектами.
 * Используется MapStruct для автоматического преобразования полей сущностей,
 * которые могут быть представлены как JsonNullable в DTO.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class JsonNullableMapper {

    /**
     * Оборачивает обычный объект в JsonNullable.
     *
     * @param entity объект, который нужно обернуть
     * @param <T>    тип объекта
     * @return объект JsonNullable, содержащий переданный объект
     */
    public <T> JsonNullable<T> wrap(T entity) {
        return JsonNullable.of(entity);
    }

    /**
     * Извлекает значение из JsonNullable.
     *
     * @param jsonNullable объект JsonNullable, из которого нужно извлечь значение
     * @param <T>          тип значения
     * @return извлечённое значение или null, если JsonNullable равен null или не содержит значения
     */
    public <T> T unwrap(JsonNullable<T> jsonNullable) {
        return jsonNullable == null ? null : jsonNullable.orElse(null);
    }

    /**
     * Проверяет, содержит ли JsonNullable значение.
     * Используется MapStruct как условие для выполнения преобразований.
     *
     * @param nullable объект JsonNullable для проверки
     * @param <T>      тип значения
     * @return true, если JsonNullable не равен null и содержит значение, иначе false
     */
    @Condition
    public <T> boolean isPresent(JsonNullable<T> nullable) {
        return nullable != null && nullable.isPresent();
    }
}
