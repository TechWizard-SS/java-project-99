package hexlet.code;

import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


/**
 * Интеграционные тесты для контроллера {@link hexlet.code.controller.UserController}.
 * Проверяют основные CRUD-операции (список, создание, обновление) пользователей
 * и защиту от неаутентифицированных запросов на удаление.
 * Использует {@link BaseTest} для подготовки инфраструктуры и токена аутентификации.
 * Некоторые тесты требуют аутентификации (токен передаётся в заголовке Authorization).
 */
public class UserControllerTest extends BaseTest {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskStatusRepository taskStatusRepository;
    /**
     * Тестирует получение списка всех пользователей.
     * Отправляет GET-запрос к '/api/users' с токеном аутентификации.
     * Проверяет, что запрос возвращает статус 200 OK.
     */
    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/users").header("Authorization", token))
                .andExpect(status().isOk());
    }

    /**
     * Тестирует создание нового пользователя.
     * Отправляет POST-запрос с данными нового пользователя (email, firstName, lastName, password).
     * Проверяет, что запрос возвращает статус 201 Created
     * и новый пользователь появляется в базе данных с корректными данными.
     */
    @Test
    public void testCreateUser() throws Exception {
        var data = Map.of(
                "email", "newuser@gmail.com",
                "firstName", "John",
                "lastName", "Doe",
                "password", "secret123"
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail("newuser@gmail.com").orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("John");
    }

    /**
     * Тестирует обновление существующего пользователя.
     * Сначала получает тестового пользователя из базы данных.
     * Затем отправляет PUT-запрос с новыми данными (только firstName) для этого пользователя.
     * Проверяет, что запрос возвращает статус 200 OK
     * и данные пользователя в базе данных обновились.
     */
    @Test
    public void testUpdateUser() throws Exception {
        var user = userRepository.findByEmail("hexlet1@example.com").get();
        var data = Map.of("firstName", "UpdatedName");

        mockMvc.perform(put("/api/users/" + user.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isOk());

        var updatedUser = userRepository.findById(user.getId()).get();
        assertThat(updatedUser.getFirstName()).isEqualTo("UpdatedName");
    }

    /**
     * Тестирует защиту от неаутентифицированных запросов на удаление пользователя.
     * Сначала получает тестового пользователя из базы данных.
     * Затем отправляет DELETE-запрос для удаления этого пользователя БЕЗ заголовка Authorization.
     * Проверяет, что запрос возвращает статус 401 Unauthorized.
     */
    @Test
    public void testDeleteUserUnauthenticated() throws Exception {
        var user = userRepository.findByEmail("hexlet1@example.com").get();

        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isUnauthorized());
    }


    //---------------------------------

    @Test
    void createUserWithDuplicateEmailShouldReturnNotFound() throws Exception {
        User user = new User();
        user.setFirstName("Hex");
        user.setLastName("Let");
        user.setEmail("test@mail.com");
        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);

        String json = "{"
                + "\"firstName\":\"Hex\","
                + "\"lastName\":\"Let\","
                + "\"email\":\"test@mail.com\","
                + "\"password\":\"123456\""
                + "}";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                .header("Authorization", token))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteUserWithTasksShouldReturnNotFound() throws Exception {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setPassword(passwordEncoder.encode("123"));
        user = userRepository.save(user);

        TaskStatus status = new TaskStatus();
        status.setName("New");
        status.setSlug("new");
        status = taskStatusRepository.save(status);

        Task task = new Task();
        task.setName("Task1");
        task.setTaskStatus(status);
        task.setAssignee(user);
        taskRepository.save(task);

        mockMvc.perform(delete("/api/users/" + user.getId())
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

}
