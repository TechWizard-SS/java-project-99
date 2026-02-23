package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.config.JwtUtil;
import hexlet.code.config.MyUserDetailsService;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;

/**
 * Абстрактный базовый класс для интеграционных тестов.
 * Предоставляет общую инфраструктуру для тестирования контроллеров:
 * - MockMvc для выполнения HTTP-запросов
 * - ObjectMapper для сериализации/десериализации JSON
 * - Репозитории для подготовки и проверки данных
 * - Утилиты для работы с JWT-токенами
 * - Преднастроенный токен аутентификации для тестов, требующих авторизации
 * Аннотация @Transactional гарантирует, что каждая транзакция в тесте будет откачена,
 * оставляя базу данных в чистом состоянии.
 */
@SpringBootTest(properties = "ADMIN_PASSWORD=password123")
@AutoConfigureMockMvc
@Transactional
public abstract class BaseTest {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper om; // Для преобразования DTO в JSON

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected JwtUtil jwtUtil;

    protected String token;


    /**
     * Создаёт JWT-токен для указанного пользователя.
     * Используется для имитации входа в систему в тестах.
     *
     * @param user объект пользователя {@link User}, для которого генерируется токен
     * @return строка токена в формате "Bearer {token}"
     */
    protected String getAuthToken(User user) {
        // Создаем UserDetails на основе сохраненного объекта, не дергая БД лишний раз
        var userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
        return "Bearer " + jwtUtil.generateToken(userDetails);
    }

    /**
     * Подготовка данных перед каждым тестовым методом.
     * Проверяет наличие тестового пользователя в базе данных.
     * Если пользователя нет, создаёт его.
     * Затем генерирует и сохраняет токен аутентификации для этого пользователя.
     */
    @BeforeEach
    public void setup() {
        var user = userRepository.findByEmail("hexlet1@example.com")
                .orElseGet(() -> {
                    var newUser = new User();
                    newUser.setEmail("hexlet1@example.com");
                    newUser.setPassword("password");
                    return userRepository.save(newUser);
                });
        token = getAuthToken(user); // Передаем объект, а не email
    }
}
