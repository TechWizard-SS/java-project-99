package hexlet.code.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;
import org.mapstruct.Named;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Маппер для преобразования между идентификаторами сущностей (ID) и самими объектами сущностей.
 * Используется MapStruct для автоматического преобразования полей в DTO,
 * которые представляют связанные сущности (например, User assigneeId -> User assignee).
 * Требует доступ к EntityManager для загрузки сущностей по ID.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ReferenceMapper {

    @Autowired
    private EntityManager entityManager;

    /**
     * Преобразует идентификатор сущности в объект сущности, загружая его из базы данных.
     * Используется как именованный метод маппинга в MapStruct.
     *
     * @param id          идентификатор сущности для загрузки
     * @param entityClass класс типа сущности
     * @param <T>         тип сущности
     * @return объект сущности или null, если ID равен null или сущность не найдена
     */
    @Named("toEntity")
    public <T> T toEntity(Long id, @TargetType Class<T> entityClass) {
        return id != null ? entityManager.find(entityClass, id) : null;
    }
}
