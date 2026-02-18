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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TaskControllerTest extends BaseTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    private TaskStatus testStatus;
    private Label testLabel;

    @BeforeEach
    public void init() {
        // Создаем статус для привязки к задаче
        testStatus = new TaskStatus();
        testStatus.setName("Draft");
        testStatus.setSlug("draft");
        taskStatusRepository.save(testStatus);

        // Создаем метку
        testLabel = new Label();
        testLabel.setName("bug");
        labelRepository.save(testLabel);
    }

    @Test
    public void testCreateTask() throws Exception {
        var data = Map.of(
                "title", "Test Task",
                "content", "Test Description",
                "status", "draft", // Передаем слаг, как в ТЗ
                "assignee_id", userRepository.findByEmail("hexlet@example.com").get().getId(),
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
        assertThat(task.getTaskStatus().getSlug()).isEqualTo("draft");
        assertThat(task.getLabels()).extracting(Label::getName).contains("bug");
    }

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

    // Бонусный тест на твой TaskSpecification (фильтрация)
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

    @Test
    public void testUnauthenticated() throws Exception {
        // Пытаемся создать задачу БЕЗ заголовка Authorization
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("title", "fail"))))
                .andExpect(status().isUnauthorized()); // 401
    }
}
