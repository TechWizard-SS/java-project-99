package hexlet.code;

import hexlet.code.model.Task;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.LabelRepository;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import java.util.Map;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционные тесты для контроллера {@link hexlet.code.controller.TaskController}.
 * Проверяют основные CRUD-операции (создание, просмотр, обновление) задач,
 * а также фильтрацию задач и защиту от неаутентифицированных запросов.
 * Использует {@link BaseTest} для подготовки инфраструктуры и токена аутентификации.
 * Все тесты, кроме {@link #testUnauthenticated()}, требуют аутентификации.
 */
public class TaskControllerTest extends BaseTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    private TaskStatus testStatus;
    private Label testLabel;


    /**
     * Подготовка данных перед каждым тестовым методом.
     * Создаёт тестовые статус задачи и метку, которые будут использоваться в тестах.
     */
    @BeforeEach
    public void init() {
        // Создаем статус для привязки к задаче
        testStatus = new TaskStatus();
        testStatus.setName("Draft1");
        testStatus.setSlug("draft1");
        taskStatusRepository.save(testStatus);

        // Создаем метку
        testLabel = new Label();
        testLabel.setName("bug1");
        labelRepository.save(testLabel);
    }

    /**
     * Тестирует создание новой задачи.
     * Отправляет POST-запрос с данными новой задачи (название, описание, статус, исполнитель, метки).
     * Проверяет, что запрос возвращает статус 201 Created
     * и новая задача появляется в базе данных с корректными данными.
     */
    @Test
    public void testCreateTask() throws Exception {
        var data = Map.of(
                "title", "Test Task",
                "content", "Test Description",
                "status", "draft1",
                "assignee_id", userRepository.findByEmail("hexlet1@example.com").get().getId(),
                "taskLabelIds", Set.of(testLabel.getId())
        );

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", token) // Используем токен из BaseTest
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isCreated());

        var task = taskRepository.findByName("Test Task").get();
        assertThat(task).isNotNull();
        assertThat(task.getDescription()).isEqualTo("Test Description");
        assertThat(task.getTaskStatus().getSlug()).isEqualTo("draft1");
        assertThat(task.getLabels()).extracting(Label::getName).contains("bug1");
    }

    /**
     * Тестирует получение конкретной задачи по её идентификатору.
     * Сначала создаёт задачу в базе данных.
     * Затем отправляет GET-запрос для получения задачи.
     * Проверяет, что запрос возвращает статус 200 OK
     * и в теле JSON содержатся ожидаемые поля (title, content).
     */
    @Test
    public void testShowTask() throws Exception {
        // 1. Создаем задачу в БД
        var task = new Task();
        task.setName("Specific Task");
        task.setDescription("Specific Description");
        task.setTaskStatus(testStatus); // testStatus из твоего @BeforeEach
        taskRepository.save(task);

        // 2. Выполняем GET запрос
        mockMvc.perform(get("/api/tasks/" + task.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                // Проверяем, что в JSON пришли правильные поля title и content
                .andExpect(jsonPath("$.title").value("Specific Task"))
                .andExpect(jsonPath("$.content").value("Specific Description"));
    }

    /**
     * Тестирует частичное обновление задачи.
     * Сначала создаёт задачу в базе данных.
     * Затем отправляет PUT-запрос, изменяя только одно поле (title).
     * Проверяет, что запрос возвращает статус 200 OK
     * и в базе данных обновилось только указанное поле (title), а другие (description) остались без изменений.
     * Это проверяет логику маппера с использованием JsonNullable.
     */
    @Test
    public void testUpdateTask() throws Exception {
        // 1. Создаем задачу в базе
        var task = new Task();
        task.setName("Initial Name");
        task.setDescription("Initial Description");
        task.setTaskStatus(testStatus);
        taskRepository.save(task);

        // 2. Данные для обновления (меняем только title)
        // Благодаря JsonNullable в твоем маппере, описание не должно затереться
        var data = Map.of("title", "Updated Name");

        mockMvc.perform(put("/api/tasks/" + task.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isOk());

        // 3. Проверяем изменения в базе
        var updatedTask = taskRepository.findById(task.getId()).get();
        assertThat(updatedTask.getName()).isEqualTo("Updated Name"); // Изменилось
        assertThat(updatedTask.getDescription()).isEqualTo("Initial Description"); // Осталось прежним!
    }

    /**
     * Тестирует фильтрацию задач.
     * Сначала создаёт задачу с определённым именем.
     * Затем отправляет GET-запрос с параметрами фильтрации (по части названия и слагу статуса).
     * Проверяет, что запрос возвращает статус 200 OK
     * и в теле ответа содержится созданная задача.
     * Это проверяет работу {@link hexlet.code.component.TaskSpecification}.
     */
    @Test
    public void testFilterTasks() throws Exception {
        var task = new Task();
        task.setName("SearchMe");
        task.setTaskStatus(testStatus);
        taskRepository.save(task);

        // Фильтруем по части названия и слагу статуса
        mockMvc.perform(get("/api/tasks?titleCont=Search&status=" + testStatus.getSlug())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("SearchMe"));
    }

    /**
     * Тестирует защиту от неаутентифицированных запросов.
     * Отправляет POST-запрос для создания задачи без заголовка Authorization.
     * Проверяет, что запрос возвращает статус 401 Unauthorized.
     */
    @Test
    public void testUnauthenticated() throws Exception {
        // Пытаемся создать задачу БЕЗ заголовка Authorization
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("title", "fail"))))
                .andExpect(status().isUnauthorized()); // 401
    }


    //---------------------

    @Test
    void getTaskNotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/999999")
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTaskNotFound() throws Exception {
        String json = "{\n"
                + "  \"name\": \"Updated\"\n"
                + "}";

        mockMvc.perform(put("/api/tasks/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTaskNotFound() throws Exception {
        mockMvc.perform(delete("/api/tasks/999999")
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}
