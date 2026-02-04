package hexlet.code.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;
import org.mapstruct.Named;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper for converting between entity IDs and entity objects.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ReferenceMapper {

    @Autowired
    private EntityManager entityManager;

    /**
     * Converts an entity ID to an entity object.
     *
     * @param id          the entity ID
     * @param entityClass the class of the entity
     * @param <T>         the type of the entity
     * @return the entity object or null if ID is null
     */
    @Named("toEntity")
    public <T> T toEntity(Long id, @TargetType Class<T> entityClass) {
        return id != null ? entityManager.find(entityClass, id) : null;
    }
}
