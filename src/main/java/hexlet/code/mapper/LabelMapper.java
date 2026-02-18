package hexlet.code.mapper;

import hexlet.code.model.Label;
import hexlet.code.model.dto.Label.LabelCreateDTO;
import hexlet.code.model.dto.Label.LabelDTO;
import hexlet.code.model.dto.Label.LabelUpdateDTO;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;


/**
 * Маппер для преобразования между сущностью {@link Label} и её DTO ({@link LabelDTO}, {@link LabelCreateDTO},
 * {@link LabelUpdateDTO}).
 * Использует MapStruct для генерации реализации.
 * Использует вспомогательные мапперы {@link JsonNullableMapper} и {@link ReferenceMapper}.
 */
@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class LabelMapper {

    /**
     * Преобразует DTO {@link LabelCreateDTO} в сущность {@link Label}.
     * Используется при создании новой метки.
     *
     * @param dto DTO с данными для создания метки
     * @return новая сущность {@link Label}
     */
    public abstract Label map(LabelCreateDTO dto);

    /**
     * Преобразует сущность {@link Label} в DTO {@link LabelDTO}.
     * Используется при возврате информации о метке.
     *
     * @param model сущность метки
     * @return DTO {@link LabelDTO} с данными метки
     */
    public abstract LabelDTO map(Label model);

    /**
     * Обновляет существующую сущность {@link Label} на основе данных из DTO {@link LabelUpdateDTO}.
     * Используется при обновлении метки.
     *
     * @param dto   DTO с новыми данными метки
     * @param model сущность метки для обновления
     */
    public abstract void update(LabelUpdateDTO dto, @MappingTarget Label model);
}
