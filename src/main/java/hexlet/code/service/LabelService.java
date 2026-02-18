package hexlet.code.service;

import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.dto.Label.LabelCreateDTO;
import hexlet.code.model.dto.Label.LabelDTO;
import hexlet.code.model.dto.Label.LabelUpdateDTO;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Сервис для управления метками ({@link Label}).
 * Предоставляет методы для получения, создания, обновления и удаления меток.
 * Использует репозитории {@link LabelRepository} и {@link TaskRepository} для взаимодействия с базой данных.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;
    private final TaskRepository taskRepository;
    private final LabelMapper mapper;

    /**
     * Возвращает список всех меток.
     *
     * @return список DTO всех меток {@link LabelDTO}
     */
    public List<LabelDTO> getAll() {
        return labelRepository.findAll().stream().map(mapper::map).toList();
    }

    /**
     * Находит метку по её идентификатору.
     *
     * @param id идентификатор метки
     * @return DTO найденной метки {@link LabelDTO}
     * @throws ResourceNotFoundException если метка с указанным идентификатором не найдена
     */
    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
        return mapper.map(label);
    }

    /**
     * Создаёт новую метку.
     *
     * @param labelData DTO с данными для создания метки {@link LabelCreateDTO}
     * @return DTO созданной метки {@link LabelDTO}
     */
    @Transactional
    public LabelDTO create(LabelCreateDTO labelData) {
        var label = mapper.map(labelData);
        labelRepository.save(label);
        return mapper.map(label);
    }

    /**
     * Обновляет существующую метку.
     *
     * @param labelData DTO с новыми данными метки {@link LabelUpdateDTO}
     * @param id        идентификатор обновляемой метки
     * @return DTO обновлённой метки {@link LabelDTO}
     * @throws ResourceNotFoundException если метка с указанным идентификатором не найдена
     */
    @Transactional
    public LabelDTO update(LabelUpdateDTO labelData, Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
        mapper.update(labelData, label);
        labelRepository.save(label);
        return mapper.map(label);
    }

    /**
     * Удаляет метку по её идентификатору.
     * Перед удалением проверяет, используется ли метка в каких-либо задачах.
     *
     * @param id идентификатор удаляемой метки
     * @throws ResourceNotFoundException если метка используется в задачах или не найдена
     */
    @Transactional
    public void delete(Long id) {
        if (taskRepository.existsByLabelsId(id)) {
            throw new ResourceNotFoundException("Cannot delete label: it is used in tasks");
        }
        labelRepository.deleteById(id);
    }
}
