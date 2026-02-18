package hexlet.code.config;

import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Сервис, реализующий интерфейс {@link UserDetailsService}.
 * Используется Spring Security для загрузки деталей пользователя
 * по его имени пользователя (в данном случае, по email) из базы данных.
 */
@Service
@RequiredArgsConstructor
public final class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Загружает данные пользователя из базы данных по его имени пользователя (email).
     * Если пользователь не найден, выбрасывает {@link UsernameNotFoundException}.
     * Возвращает объект {@link UserDetails}, содержащий имя пользователя (email),
     * закодированный пароль и роли (в данном случае, "ROLE_USER").
     *
     * @param username имя пользователя (email), по которому производится поиск
     * @return объект {@link UserDetails}, представляющий аутентифицированного пользователя
     * @throws UsernameNotFoundException если пользователь с указанным email не найден в базе данных
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ищем в базе по email
        return userRepository.findByEmail(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .authorities("ROLE_USER")
                        .build()
                )
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
