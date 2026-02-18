package hexlet.code.service;

import hexlet.code.component.TaskSpecification;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.dto.Task.TaskCreateDTO;
import hexlet.code.model.dto.Task.TaskDTO;
import hexlet.code.model.dto.Task.TaskUpdateDTO;
import hexlet.code.model.dto.TaskParamsDTO;
import hexlet.code.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Сервис для управления задачами ({@link Task}).
 * Предоставляет методы для получения списка задач (с фильтрацией), получения,
 * создания, обновления и удаления отдельных задач.
 * Использует репозиторий {@link TaskRepository} и спецификацию {@link TaskSpecification}
 * для взаимодействия с базой данных и фильтрации.
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskSpecification taskSpecification;
    private final TaskMapper mapper;

    /**
     * Возвращает список задач, отфильтрованный по указанным параметрам.
     *
     * @param params объект {@link TaskParamsDTO}, содержащий параметры фильтрации (может быть пустым)
     * @return список DTO задач {@link TaskDTO}, удовлетворяющих фильтру
     */
    public List<TaskDTO> getAll(TaskParamsDTO params) {
        var spec = taskSpecification.build(params);
        var tasks = taskRepository.findAll(spec);
        return tasks.stream().map(mapper::map).toList();
    }

    /**
     * Находит задачу по её идентификатору.
     *
     * @param id идентификатор задачи
     * @return DTO найденной задачи {@link TaskDTO}
     * @throws ResourceNotFoundException если задача с указанным идентификатором не найдена
     */
    public TaskDTO findById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return mapper.map(task);
    }

    /**
     * Создаёт новую задачу.
     *
     * @param taskData DTO с данными для создания задачи {@link TaskCreateDTO}
     * @return DTO созданной задачи {@link TaskDTO}
     */
    @Transactional
    public TaskDTO create(TaskCreateDTO taskData) {
        var task = mapper.map(taskData);
        taskRepository.save(task);
        return mapper.map(task);
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param taskData DTO с новыми данными задачи {@link TaskUpdateDTO}
     * @param id       идентификатор обновляемой задачи
     * @return DTO обновлённой задачи {@link TaskDTO}
     * @throws ResourceNotFoundException если задача с указанным идентификатором не найдена
     */
    @Transactional
    public TaskDTO update(TaskUpdateDTO taskData, Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        mapper.update(taskData, task);
        taskRepository.save(task);
        return mapper.map(task);
    }

    /**
     * Удаляет задачу по её идентификатору.
     *
     * @param id идентификатор удаляемой задачи
     * @throws ResourceNotFoundException если задача с указанным идентификатором не найдена
     */
    @Transactional
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found");
        }
        taskRepository.deleteById(id);
    }
}
