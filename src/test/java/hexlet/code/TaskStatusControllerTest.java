package hexlet.code;

import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import hexlet.code.util.NamedRoutes;
import java.util.Map;

/**
 * Интеграционные тесты для контроллера {@link hexlet.code.controller.TaskStatusController}.
 * Проверяют корректную обработку ошибок, например, при попытке создания статуса задачи
 * с уже существующим слагом.
 * Использует {@link BaseTest} для подготовки инфраструктуры и токена аутентификации.
 */
public class TaskStatusControllerTest extends BaseTest {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    /**
     * Тестирует создание статуса задачи с дублирующимся слагом.
     * Сначала создаёт в базе данных один статус с определённым слагом.
     * Затем отправляет POST-запрос для создания другого статуса с тем же слагом.
     * Проверяет, что запрос возвращает статус 404 Not Found,
     * что соответствует выбрасываемому в сервисе исключению {@link hexlet.code.exception.ResourceNotFoundException}
     * при попытке создать статус с уже существующим слагом.
     */
    @Test
    public void testCreateStatusWithDuplicateSlug() throws Exception {
        var status = new TaskStatus();
        status.setName("Existing");
        status.setSlug("existing_slug");
        taskStatusRepository.save(status);

        var data = Map.of("name", "New", "slug", "existing_slug");

        mockMvc.perform(post(NamedRoutes.TASK_STATUSES)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isNotFound());
    }
}
