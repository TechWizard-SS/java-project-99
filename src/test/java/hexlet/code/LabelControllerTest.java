package hexlet.code;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Интеграционные тесты для контроллера {@link hexlet.code.controller.LabelController}.
 * Проверяют основные CRUD-операции (список, просмотр, создание, обновление, удаление) меток.
 * Использует {@link BaseTest} для подготовки инфраструктуры и токена аутентификации.
 * Все тесты требуют аутентификации (токен передаётся в заголовке Authorization).
 */
public class LabelControllerTest extends BaseTest {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    private Label testLabel;

    @Autowired
    private TaskStatusRepository taskStatusRepository;



    /**
     * Подготовка данных перед каждым тестовым методом.
     * Проверяет наличие тестовой метки 'bug1' в базе данных.
     * Если метки нет, создаёт её.
     */
    @BeforeEach
    public void init() {
        testLabel = labelRepository.findByName("bug1")
                .orElseGet(() -> {
                    var label = new Label();
                    label.setName("bug1");
                    return labelRepository.save(label);
                });
    }

    /**
     * Тестирует получение списка всех меток.
     * Проверяет, что запрос возвращает статус 200 OK,
     * содержит заголовок X-Total-Count и тело ответа содержит имя тестовой метки.
     */
    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/labels").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThat(body).contains("bug1");
    }

    /**
     * Тестирует получение конкретной метки по её идентификатору.
     * Проверяет, что запрос возвращает статус 200 OK
     * и в теле JSON содержится поле 'name' с ожидаемым значением.
     */
    @Test
    public void testShow() throws Exception {
        mockMvc.perform(get("/api/labels/" + testLabel.getId()).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("bug1"));
    }

    /**
     * Тестирует создание новой метки.
     * Отправляет POST-запрос с данными новой метки.
     * Проверяет, что запрос возвращает статус 201 Created
     * и новая метка появляется в базе данных.
     */
    @Test
    public void testCreate() throws Exception {
        var data = Map.of("name", "feature1");

        mockMvc.perform(post("/api/labels")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isCreated());

        var label = labelRepository.findByName("feature1").orElse(null);
        assertThat(label).isNotNull();
    }

    /**
     * Тестирует обновление существующей метки.
     * Отправляет PUT-запрос с новыми данными для тестовой метки.
     * Проверяет, что запрос возвращает статус 200 OK
     * и данные метки в базе данных обновились.
     */
    @Test
    public void testUpdate() throws Exception {
        var data = Map.of("name", "crit-bug");

        mockMvc.perform(put("/api/labels/" + testLabel.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isOk());

        var updatedLabel = labelRepository.findById(testLabel.getId()).get();
        assertThat(updatedLabel.getName()).isEqualTo("crit-bug");
    }

    /**
     * Тестирует удаление существующей метки.
     * Отправляет DELETE-запрос для тестовой метки.
     * Проверяет, что запрос возвращает статус 204 No Content
     * и метка больше не существует в базе данных.
     */
    @Test
    public void testDestroy() throws Exception {
        mockMvc.perform(delete("/api/labels/" + testLabel.getId())
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        assertThat(labelRepository.existsById(testLabel.getId())).isFalse();
    }

    @Test
    void deleteLabelUsedInTaskShouldReturnNotFound() throws Exception {
        Label label = new Label();
        label.setName("bug2");
        label = labelRepository.save(label);

        TaskStatus status = new TaskStatus();
        status.setName("New2");
        status.setSlug("new2");
        status = taskStatusRepository.save(status);

        Task task = new Task();
        task.setName("Task1");
        task.setTaskStatus(status);
        task.getLabels().add(label);
        taskRepository.save(task);

        mockMvc.perform(delete("/api/labels/" + label.getId())
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}
