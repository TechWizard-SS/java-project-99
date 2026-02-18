package hexlet.code.controller;

import hexlet.code.model.dto.Task.TaskCreateDTO;
import hexlet.code.model.dto.Task.TaskDTO;
import hexlet.code.model.dto.Task.TaskUpdateDTO;
import hexlet.code.model.dto.TaskParamsDTO;
import hexlet.code.service.TaskService;
import hexlet.code.util.NamedRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Контроллер для управления задачами ({@link hexlet.code.model.Task}).
 * Обрабатывает HTTP-запросы для получения списка задач (с фильтрацией),
 * просмотра, создания, обновления и удаления отдельных задач.
 */
@RestController
@RequestMapping(NamedRoutes.TASKS)
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Обрабатывает GET-запрос на получение списка задач.
     * Поддерживает фильтрацию задач через параметры {@code params}.
     * Возвращает список DTO задач и заголовок X-Total-Count.
     *
     * @param params объект {@link TaskParamsDTO}, содержащий параметры фильтрации (может быть пустым)
     * @return {@link ResponseEntity} с HTTP статусом 200 OK и телом, содержащим список {@link TaskDTO}
     */
    @GetMapping("")
    public ResponseEntity<List<TaskDTO>> index(TaskParamsDTO params) {
        var tasks = taskService.getAll(params);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .body(tasks);
    }

    /**
     * Обрабатывает GET-запрос на получение задачи по её идентификатору.
     *
     * @param id идентификатор задачи
     * @return DTO найденной задачи {@link TaskDTO}
     */
    @GetMapping(NamedRoutes.TASK_ID)
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO show(@PathVariable Long id) {
        return taskService.findById(id);
    }

    /**
     * Обрабатывает POST-запрос на создание новой задачи.
     * Принимает DTO с данными для создания.
     *
     * @param taskData DTO {@link TaskCreateDTO} с данными новой задачи
     * @return DTO созданной задачи {@link TaskDTO}
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@Valid @RequestBody TaskCreateDTO taskData) {
        return taskService.create(taskData);
    }

    /**
     * Обрабатывает PUT-запрос на обновление существующей задачи.
     * Принимает DTO с новыми данными и идентификатор задачи.
     *
     * @param taskData DTO {@link TaskUpdateDTO} с новыми данными задачи
     * @param id       идентификатор обновляемой задачи
     * @return DTO обновлённой задачи {@link TaskDTO}
     */
    @PutMapping(NamedRoutes.TASK_ID)
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO update(@Valid @RequestBody TaskUpdateDTO taskData, @PathVariable Long id) {
        return taskService.update(taskData, id);
    }

    /**
     * Обрабатывает DELETE-запрос на удаление задачи по её идентификатору.
     *
     * @param id идентификатор удаляемой задачи
     */
    @DeleteMapping(NamedRoutes.TASK_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        taskService.delete(id);
    }
}
