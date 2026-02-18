package hexlet.code.util;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Компонент для начальной загрузки (инициализации) данных при запуске приложения.
 * Создаёт предопределённых пользователей (например, администраторов), если они ещё не существуют в базе данных.
 * Пароли пользователей хешируются с использованием {@link PasswordEncoder}.
 */
@Component
@RequiredArgsConstructor
public final class DataLoader {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Метод, выполняющийся сразу после создания бина и внедрения зависимостей.
     * Проверяет, существуют ли пользователи с заранее известными email в базе данных.
     * Если нет, создаёт и сохраняет в базу двух тестовых/административных пользователей.
     * Их пароли предварительно хешируются.
     */
    @PostConstruct
    public void init() {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("hexlet@example.com");
            admin.setPassword(passwordEncoder.encode("qwerty"));
            admin.setFirstName("Admin");
            userRepository.save(admin);


            User admin2 = new User();
            admin2.setEmail("hehe@example.com");
            admin2.setPassword(passwordEncoder.encode("admin"));
            admin2.setFirstName("Admin2");
            admin2.setLastName("User2");
            userRepository.save(admin2);
        }
    }
}
