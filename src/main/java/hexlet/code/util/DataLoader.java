package hexlet.code.util;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Компонент для начальной загрузки данных при запуске приложения.
 * Создаёт предопределённых пользователей, если они ещё не существуют в базе данных.
 * Пароли пользователей хешируются с использованием {@link PasswordEncoder}.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Spring возьмет значение из переменной ADMIN_PASSWORD.
    // Если она не задана, выдаст ошибку при старте.
    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            User admin2 = new User();
            admin2.setEmail("hexlet@example.com");
            admin2.setPassword(passwordEncoder.encode(adminPassword));
            admin2.setFirstName("Admin");
            admin2.setLastName("User");
            userRepository.save(admin2);
        }
    }
}
