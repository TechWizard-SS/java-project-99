package hexlet.code;

import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import hexlet.code.util.NamedRoutes;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    //--------------------------------------------------------

    @Test
    void updateTaskStatusWithDuplicateSlugShouldReturnNotFound() throws Exception {
        TaskStatus status1 = new TaskStatus();
        status1.setName("Status1");
        status1.setSlug("slug1");
        status1 = taskStatusRepository.save(status1);

        TaskStatus status2 = new TaskStatus();
        status2.setName("Status2");
        status2.setSlug("slug2");
        taskStatusRepository.save(status2);

        String json = "{\n"
                + "  \"slug\": \"slug2\"\n"
                + "}";

        mockMvc.perform(put("/api/task_statuses/" + status1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTaskStatusUsedInTaskShouldReturnNotFound() throws Exception {
        TaskStatus status = new TaskStatus();
        status.setName("New");
        status.setSlug("new");
        status = taskStatusRepository.save(status);

        Task task = new Task();
        task.setName("Task1");
        task.setTaskStatus(status);
        taskRepository.save(task);

        mockMvc.perform(delete("/api/task_statuses/" + status.getId())
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }


    @Test
    void updateUserPasswordShouldChangePassword() throws Exception {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setPassword(passwordEncoder.encode("oldpass"));
        user = userRepository.save(user);

        String userToken = getAuthToken(user);

        String json = "{\n"
                + "  \"password\": \"newpass\"\n"
                + "}";

        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", userToken))
                .andExpect(status().isOk());

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(passwordEncoder.matches("newpass", updated.getPassword())).isTrue();
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/task_statuses").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"));
    }

    @Test
    public void testShow() throws Exception {
        var status = new TaskStatus();
        status.setName("SpecificStatus");
        status.setSlug("specific_slug");
        taskStatusRepository.save(status);

        mockMvc.perform(get("/api/task_statuses/" + status.getId()).header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    public void testShowBySlug() throws Exception {
        var status = new TaskStatus();
        status.setName("BySlug");
        status.setSlug("find_me");
        taskStatusRepository.save(status);

        mockMvc.perform(get("/api/task_statuses/slug/find_me").header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateSuccess() throws Exception {
        var data = Map.of("name", "Active", "slug", "active");

        mockMvc.perform(post("/api/task_statuses")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isCreated());

        var status = taskStatusRepository.findBySlug("active");
        assertThat(status).isPresent();
    }

    @Test
    public void testUpdateSuccess() throws Exception {
        var status = new TaskStatus();
        status.setName("OldName");
        status.setSlug("old_slug");
        taskStatusRepository.save(status);

        var data = Map.of("name", "NewName"); // Обновляем только имя, не трогая слаг

        mockMvc.perform(put("/api/task_statuses/" + status.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isOk());

        var updatedStatus = taskStatusRepository.findById(status.getId()).get();
        assertThat(updatedStatus.getName()).isEqualTo("NewName");
        assertThat(updatedStatus.getSlug()).isEqualTo("old_slug");
    }

    @Test
    public void testDeleteSuccess() throws Exception {
        var status = new TaskStatus();
        status.setName("To Delete");
        status.setSlug("to_delete");
        taskStatusRepository.save(status);

        mockMvc.perform(delete("/api/task_statuses/" + status.getId())
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.existsById(status.getId())).isFalse();
    }
}
