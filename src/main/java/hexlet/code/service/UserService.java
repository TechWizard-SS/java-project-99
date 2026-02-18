package hexlet.code.service;

import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.dto.User.UserCreateDTO;
import hexlet.code.model.dto.User.UserDTO;
import hexlet.code.model.dto.User.UserUpdateDTO;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Сервис для управления пользователями ({@link User}).
 * Предоставляет методы для получения списка пользователей, получения,
 * создания, обновления и удаления отдельных пользователей.
 * Проверяет уникальность email при создании и наличии связанных задач при удалении.
 * Использует репозитории {@link UserRepository} и {@link TaskRepository},
 * а также {@link PasswordEncoder} для хеширования паролей.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TaskRepository taskRepository;

    /**
     * Возвращает список всех пользователей.
     *
     * @return список DTO всех пользователей {@link UserDTO}
     */
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream().map(userMapper::map).toList();
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return DTO найденного пользователя {@link UserDTO}
     * @throws ResourceNotFoundException если пользователь с указанным идентификатором не найден
     */
    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.map(user);
    }

    /**
     * Создаёт нового пользователя.
     * Перед созданием проверяет, что email уникален.
     * Пароль хешируется перед сохранением.
     *
     * @param userData DTO с данными для создания пользователя {@link UserCreateDTO}
     * @return DTO созданного пользователя {@link UserDTO}
     * @throws ResourceNotFoundException если пользователь с таким email уже существует
     */
    @Transactional
    public UserDTO create(UserCreateDTO userData) {
        if (userRepository.findByEmail(userData.getEmail()).isPresent()) {
            throw new ResourceNotFoundException("Email already exists");
        }
        var user = userMapper.map(userData);
        user.setPassword(passwordEncoder.encode(userData.getPassword()));
        userRepository.save(user);
        return userMapper.map(user);
    }

    /**
     * Обновляет существующего пользователя.
     * Если в DTO присутствует пароль (JsonNullable), он хешируется и обновляется.
     *
     * @param userData DTO с новыми данными пользователя {@link UserUpdateDTO}
     * @param id       идентификатор обновляемого пользователя
     * @return DTO обновлённого пользователя {@link UserDTO}
     * @throws ResourceNotFoundException если пользователь с указанным идентификатором не найден
     */
    @Transactional
    public UserDTO update(UserUpdateDTO userData, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userMapper.update(userData, user);

        if (userData.getPassword() != null && userData.getPassword().isPresent()) {
            user.setPassword(passwordEncoder.encode(userData.getPassword().get()));
        }

        userRepository.save(user);
        return userMapper.map(user);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     * Перед удалением проверяет, назначены ли пользователю какие-либо задачи.
     *
     * @param id идентификатор удаляемого пользователя
     * @throws ResourceNotFoundException если пользователь назначен исполнителем задач или не найден
     */
    @Transactional
    public void delete(Long id) {
        if (taskRepository.existsByAssigneeId(id)) {
            throw new ResourceNotFoundException("Cannot delete user: they are assigned to tasks");
        }
        userRepository.deleteById(id);
    }
}
