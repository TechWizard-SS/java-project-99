package hexlet.code.mapper;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.model.dto.Task.TaskCreateDTO;
import hexlet.code.model.dto.Task.TaskDTO;
import hexlet.code.model.dto.Task.TaskUpdateDTO;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Маппер для преобразования между сущностью {@link Task} и её DTO ({@link TaskDTO}, {@link TaskCreateDTO},
 * {@link TaskUpdateDTO}).
 * Обрабатывает преобразование полей, связанных сущностей (статус, исполнитель, метки),
 * используя соответствующие репозитории для загрузки объектов по ID или slug.
 * Поддерживает преобразование полей, представленных как {@link JsonNullable},
 * что характерно для DTO, используемых при частичном обновлении (PATCH).
 * Использует MapStruct для генерации реализации.
 * Использует вспомогательные мапперы {@link JsonNullableMapper} и {@link ReferenceMapper}.
 */
@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {


    // У абстрактных классов будет неявный конструктор по умолчанию (без аргументов).
    // MapStruct сможет спокойно создать Impl-классы.
    @Autowired
    private TaskStatusRepository statusRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private UserRepository userRepository;

    // View: Entity -> DTO
    /**
     * Преобразует сущность {@link Task} в DTO {@link TaskDTO}.
     * Выполняет переименование полей (name -> title, description -> content),
     * и извлекает вложенные значения (slug статуса, ID исполнителя, ID меток).
     *
     * @param model сущность задачи
     * @return DTO {@link TaskDTO} с данными задачи
     */
    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "labelIds", source = "labels", qualifiedByName = "labelsToIds")
    public abstract TaskDTO map(Task model);

    // Create: CreateDTO -> Entity
    /**
     * Преобразует DTO {@link TaskCreateDTO} в сущность {@link Task}.
     * Выполняет переименование полей и загружает связанные сущности (статус, исполнитель, метки)
     * по их ID или slug из базы данных.
     * Используется при создании новой задачи.
     *
     * @param dto DTO с данными для создания задачи
     * @return новая сущность {@link Task}
     */
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToStatusRaw")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "idToUserRaw")
    @Mapping(target = "labels", source = "labelIds", qualifiedByName = "idsToLabelsRaw")
    public abstract Task map(TaskCreateDTO dto);

    // Update: UpdateDTO -> Entity (PATCH)
    /**
     * Обновляет существующую сущность {@link Task} на основе данных из DTO {@link TaskUpdateDTO}.
     * Выполняет переименование полей и загружает связанные сущности по их ID или slug
     * только если они присутствуют в DTO (JsonNullable).
     * Используется при частичном обновлении задачи.
     *
     * @param dto   DTO с новыми данными задачи (могут быть null)
     * @param model сущность задачи для обновления
     */
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToStatus")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "idToUser")
    @Mapping(target = "labels", source = "labelIds", qualifiedByName = "idsToLabels")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

    // --- ХЕЛПЕРЫ ДЛЯ JsonNullable (Update) ---
    /**
     * Вспомогательный метод для MapStruct.
     * Загружает пользователя по ID, если JsonNullable содержит значение.
     * Используется при обновлении задачи, когда assigneeId может быть JsonNullable.
     *
     * @param userId JsonNullable, содержащий ID пользователя
     * @return объект {@link User} или null
     */
    @Named("idToUser")
    protected User idToUser(JsonNullable<Long> userId) {
//        return userId != null && userId.isPresent()
//                ? userRepository.findById(userId.get()).orElse(null) : null;
        return userId != null && userId.isPresent() && userId.get() != null
                ? userRepository.findById(userId.get()).orElse(null) : null;
    }

    /**
     * Вспомогательный метод для MapStruct.
     * Загружает статус задачи по slug, если JsonNullable содержит значение.
     * Используется при обновлении задачи, когда status может быть JsonNullable.
     *
     * @param slug JsonNullable, содержащий slug статуса
     * @return объект {@link TaskStatus} или null
     */
//    @Named("slugToStatus")
//    protected TaskStatus slugToStatus(JsonNullable<String> slug) {
//        return slug != null && slug.isPresent()
//                ? statusRepository.findBySlug(slug.get()).orElse(null) : null;
//    }
    @Named("slugToStatus")
    protected TaskStatus slugToStatus(JsonNullable<String> slug) {
        // Добавляем проверку slug.get() != null
        return slug != null && slug.isPresent() && slug.get() != null
                ? statusRepository.findBySlug(slug.get()).orElse(null) : null;
    }

    /**
     * Вспомогательный метод для MapStruct.
     * Загружает множество меток по их ID, если JsonNullable содержит значения.
     * Используется при обновлении задачи, когда labelIds может быть JsonNullable.
     *
     * @param labelIds JsonNullable, содержащий множество ID меток
     * @return множество объектов {@link Label} или null
     */
//    @Named("idsToLabels")
//    protected Set<Label> idsToLabels(JsonNullable<Set<Long>> labelIds) {
//        if (labelIds == null || !labelIds.isPresent()) {
//            return null;
//        } else {
//            return new HashSet<>(labelRepository.findAllById(labelIds.get()));
//        }
//    }

    @Named("idsToLabels")
    protected Set<Label> idsToLabels(JsonNullable<Set<Long>> labelIds) {
        if (labelIds == null || !labelIds.isPresent() || labelIds.get() == null) {
            return null;
        }
        return new HashSet<>(labelRepository.findAllById(labelIds.get()));
    }

    // --- ХЕЛПЕРЫ ДЛЯ ОБЫЧНЫХ ПОЛЕЙ (Create) ---
    /**
     * Вспомогательный метод для MapStruct.
     * Загружает пользователя по ID.
     * Используется при создании задачи, когда assigneeId не является JsonNullable.
     *
     * @param userId ID пользователя
     * @return объект {@link User} или null
     */
    @Named("idToUserRaw")
    protected User idToUserRaw(Long userId) {
        return userId != null ? userRepository.findById(userId).orElse(null) : null;
    }

    /**
     * Вспомогательный метод для MapStruct.
     * Загружает статус задачи по slug.
     * Используется при создании задачи, когда status не является JsonNullable.
     *
     * @param slug slug статуса
     * @return объект {@link TaskStatus} или null
     */
    @Named("slugToStatusRaw")
    protected TaskStatus slugToStatusRaw(String slug) {
        return slug != null ? statusRepository.findBySlug(slug).orElse(null) : null;
    }

    /**
     * Вспомогательный метод для MapStruct.
     * Загружает множество меток по их ID.
     * Используется при создании задачи, когда labelIds не является JsonNullable.
     *
     * @param labelIds множество ID меток
     * @return множество объектов {@link Label} или null
     */
    @Named("idsToLabelsRaw")
    protected Set<Label> idsToLabelsRaw(Set<Long> labelIds) {
        return labelIds != null ? new HashSet<>(labelRepository.findAllById(labelIds)) : null;
    }

    /**
     * Вспомогательный метод для MapStruct.
     * Извлекает множество ID из множества меток.
     * Используется при преобразовании сущности задачи в DTO для получения labelIds.
     *
     * @param labels множество объектов {@link Label}
     * @return множество ID меток или null
     */
    @Named("labelsToIds")
    protected Set<Long> labelsToIds(Set<Label> labels) {
        return labels == null ? null : labels.stream().map(Label::getId).collect(Collectors.toSet());
    }
}
