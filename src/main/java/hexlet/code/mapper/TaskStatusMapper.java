package hexlet.code.mapper;

import hexlet.code.model.TaskStatus;
import hexlet.code.model.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.model.dto.TaskStatus.TaskStatusDTO;
import hexlet.code.model.dto.TaskStatus.TaskStatusUpdateDTO;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Маппер для преобразования между сущностью {@link TaskStatus} и её DTO ({@link TaskStatusDTO},
 * {@link TaskStatusCreateDTO}, {@link TaskStatusUpdateDTO}).
 * Использует MapStruct для генерации реализации.
 * Использует вспомогательный маппер {@link JsonNullableMapper}.
 */
@Mapper(
        uses = { JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskStatusMapper {

    /**
     * Преобразует сущность {@link TaskStatus} в DTO {@link TaskStatusDTO}.
     * Используется при возврате информации о статусе задачи.
     *
     * @param model сущность статуса задачи
     * @return DTO {@link TaskStatusDTO} с данными статуса задачи
     */
    public abstract TaskStatusDTO map(TaskStatus model);

    /**
     * Преобразует DTO {@link TaskStatusCreateDTO} в сущность {@link TaskStatus}.
     * Используется при создании нового статуса задачи.
     *
     * @param dto DTO с данными для создания статуса задачи
     * @return новая сущность {@link TaskStatus}
     */
    public abstract TaskStatus map(TaskStatusCreateDTO dto);

    /**
     * Обновляет существующую сущность {@link TaskStatus} на основе данных из DTO {@link TaskStatusUpdateDTO}.
     * Используется при обновлении статуса задачи.
     *
     * @param dto   DTO с новыми данными статуса задачи
     * @param model сущность статуса задачи для обновления
     */
    public abstract void update(TaskStatusUpdateDTO dto, @MappingTarget TaskStatus model);
}
