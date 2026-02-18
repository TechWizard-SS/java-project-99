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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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

    // Хелпер для получения токена (имитируем вход в систему)
    protected String getAuthToken(String email) {
        var userDetails = userDetailsService.loadUserByUsername(email);
        return "Bearer " + jwtUtil.generateToken(userDetails);
    }

    @BeforeEach
    public void setup() {
        // Убеждаемся, что в базе есть хотя бы один пользователь для тестов
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            var user = new User();
            user.setEmail("hexlet@example.com");
            user.setPassword("password"); // В тестах не шифруем, так как не проверяем логин
            userRepository.save(user);
        }
        token = getAuthToken("hexlet@example.com");
    }
}
