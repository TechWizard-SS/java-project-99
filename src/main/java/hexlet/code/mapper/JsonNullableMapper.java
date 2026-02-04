package hexlet.code.mapper;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Mapper for converting between JsonNullable and regular objects.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class JsonNullableMapper {

    /**
     * Wraps an entity into JsonNullable.
     *
     * @param entity the entity to wrap
     * @param <T>    the type of the entity
     * @return JsonNullable containing the entity
     */
    public <T> JsonNullable<T> wrap(T entity) {
        return JsonNullable.of(entity);
    }

    /**
     * Unwraps a JsonNullable to get the underlying value.
     *
     * @param jsonNullable the JsonNullable to unwrap
     * @param <T>          the type of the value
     * @return the underlying value or null if JsonNullable is null
     */
    public <T> T unwrap(JsonNullable<T> jsonNullable) {
        return jsonNullable == null ? null : jsonNullable.orElse(null);
    }

    /**
     * Checks if a JsonNullable contains a present value.
     *
     * @param nullable the JsonNullable to check
     * @param <T>      the type of the value
     * @return true if the JsonNullable is not null and contains a present value
     */
    @Condition
    public <T> boolean isPresent(JsonNullable<T> nullable) {
        return nullable != null && nullable.isPresent();
    }
}
