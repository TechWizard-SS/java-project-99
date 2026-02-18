package hexlet.code.controller;

import hexlet.code.model.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.model.dto.TaskStatus.TaskStatusDTO;
import hexlet.code.model.dto.TaskStatus.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusService;
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
 * Контроллер для управления статусами задач ({@link hexlet.code.model.TaskStatus}).
 * Обрабатывает HTTP-запросы для получения списка статусов, просмотра,
 * создания, обновления, удаления отдельных статусов и получения статуса по слагу.
 */
@RestController
@RequestMapping(NamedRoutes.TASK_STATUSES)
@RequiredArgsConstructor
public final class TaskStatusController {

    private final TaskStatusService taskStatusService;

    /**
     * Обрабатывает GET-запрос на получение списка всех статусов задач.
     * Возвращает список DTO статусов и заголовок X-Total-Count.
     *
     * @return {@link ResponseEntity} с HTTP статусом 200 OK и телом, содержащим список {@link TaskStatusDTO}
     */
    @GetMapping("")
    public ResponseEntity<List<TaskStatusDTO>> index() {
        List<TaskStatusDTO> statuses = taskStatusService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(statuses.size()))
                .body(statuses);
    }

    /**
     * Обрабатывает GET-запрос на получение статуса задачи по его идентификатору.
     *
     * @param id идентификатор статуса задачи
     * @return DTO найденного статуса задачи {@link TaskStatusDTO}
     */
    @GetMapping(NamedRoutes.TASK_STATUS_ID)
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO show(@PathVariable Long id) {
        return taskStatusService.findById(id);
    }

    /**
     * Обрабатывает POST-запрос на создание нового статуса задачи.
     * Принимает DTO с данными для создания.
     *
     * @param statusData DTO {@link TaskStatusCreateDTO} с данными нового статуса задачи
     * @return DTO созданного статуса задачи {@link TaskStatusDTO}
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO statusData) {
        return taskStatusService.create(statusData);
    }

    /**
     * Обрабатывает PUT-запрос на обновление существующего статуса задачи.
     * Принимает DTO с новыми данными и идентификатор статуса.
     *
     * @param statusData DTO {@link TaskStatusUpdateDTO} с новыми данными статуса задачи
     * @param id         идентификатор обновляемого статуса задачи
     * @return DTO обновлённого статуса задачи {@link TaskStatusDTO}
     */
    @PutMapping(NamedRoutes.TASK_STATUS_ID)
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO update(@Valid @RequestBody TaskStatusUpdateDTO statusData,
                                @PathVariable Long id) {
        return taskStatusService.update(statusData, id);
    }

    /**
     * Обрабатывает DELETE-запрос на удаление статуса задачи по его идентификатору.
     *
     * @param id идентификатор удаляемого статуса задачи
     */
    @DeleteMapping(NamedRoutes.TASK_STATUS_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        taskStatusService.delete(id);
    }

    /**
     * Обрабатывает GET-запрос на получение статуса задачи по его слагу.
     * Этот метод добавлен в соответствии с требованием ТЗ.
     *
     * @param slug слаг статуса задачи
     * @return DTO найденного статуса задачи {@link TaskStatusDTO}
     */
    @GetMapping(NamedRoutes.TASK_STATUS_SLUG)
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO getBySlug(@PathVariable String slug) {
        return taskStatusService.findBySlug(slug);
    }
}
