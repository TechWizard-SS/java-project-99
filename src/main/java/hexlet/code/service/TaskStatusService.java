package hexlet.code.service;

import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.model.dto.TaskStatus.TaskStatusDTO;
import hexlet.code.model.dto.TaskStatus.TaskStatusUpdateDTO;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Сервис для управления статусами задач ({@link TaskStatus}).
 * Предоставляет методы для получения списка статусов, получения,
 * создания, обновления и удаления отдельных статусов.
 * Проверяет уникальность слага и наличие связанных задач при обновлении и удалении.
 * Использует репозитории {@link TaskStatusRepository} и {@link TaskRepository}
 * для взаимодействия с базой данных.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;
    private final TaskRepository taskRepository;

    /**
     * Возвращает список всех статусов задач.
     *
     * @return список DTO всех статусов задач {@link TaskStatusDTO}
     */
    public List<TaskStatusDTO> getAll() {
        return taskStatusRepository.findAll().stream().map(taskStatusMapper::map).toList();
    }

    /**
     * Находит статус задачи по её идентификатору.
     *
     * @param id идентификатор статуса задачи
     * @return DTO найденного статуса задачи {@link TaskStatusDTO}
     * @throws ResourceNotFoundException если статус задачи с указанным идентификатором не найден
     */
    public TaskStatusDTO findById(Long id) {
        var status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
        return taskStatusMapper.map(status);
    }

    /**
     * Создаёт новый статус задачи.
     * Перед созданием проверяет, что слаг уникален.
     *
     * @param statusData DTO с данными для создания статуса задачи {@link TaskStatusCreateDTO}
     * @return DTO созданного статуса задачи {@link TaskStatusDTO}
     * @throws ResourceNotFoundException если статус задачи с таким слагом уже существует
     */
    @Transactional
    public TaskStatusDTO create(TaskStatusCreateDTO statusData) {
        if (taskStatusRepository.findBySlug(statusData.getSlug()).isPresent()) {
            throw new ResourceNotFoundException("Slug already exists");
        }
        var status = taskStatusMapper.map(statusData);
        taskStatusRepository.save(status);
        return taskStatusMapper.map(status);
    }

    /**
     * Обновляет существующий статус задачи.
     * Перед обновлением проверяет, что новый слаг уникален (если он изменяется).
     *
     * @param statusData DTO с новыми данными статуса задачи {@link TaskStatusUpdateDTO}
     * @param id         идентификатор обновляемого статуса задачи
     * @return DTO обновлённого статуса задачи {@link TaskStatusDTO}
     * @throws ResourceNotFoundException если статус задачи с указанным идентификатором не найден
     *                                   или если новый слаг уже существует
     */
    @Transactional
    public TaskStatusDTO update(TaskStatusUpdateDTO statusData, Long id) {
        var status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));

        if (statusData.getSlug() != null && statusData.getSlug().isPresent()) {
            String newSlug = statusData.getSlug().get();
            if (!newSlug.equals(status.getSlug()) && taskStatusRepository.findBySlug(newSlug).isPresent()) {
                throw new ResourceNotFoundException("Slug already exists");
            }
        }

        taskStatusMapper.update(statusData, status);
        taskStatusRepository.save(status);
        return taskStatusMapper.map(status);
    }

    /**
     * Удаляет статус задачи по её идентификатору.
     * Перед удалением проверяет, используются ли задачи с этим статусом.
     *
     * @param id идентификатор удаляемого статуса задачи
     * @throws ResourceNotFoundException если статус задачи используется в задачах или не найден
     */
    @Transactional
    public void delete(Long id) {
        if (taskRepository.existsByTaskStatusId(id)) {
            throw new ResourceNotFoundException("Cannot delete status: it is used in tasks");
        }
        taskStatusRepository.deleteById(id);
    }

    /**
     * Находит статус задачи по её слагу.
     * Этот метод добавлен в соответствии с требованием ТЗ.
     *
     * @param slug слаг статуса задачи
     * @return DTO найденного статуса задачи {@link TaskStatusDTO}
     * @throws ResourceNotFoundException если статус задачи с указанным слагом не найден
     */
    public TaskStatusDTO findBySlug(String slug) {
        var status = taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Status with slug " + slug + " not found"));
        return taskStatusMapper.map(status);
    }
}
