package hexlet.code.service;

import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.dto.UserDTO;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.map(user);
    }

    @Transactional
    public UserDTO create(UserDTO userData) {
        if (userRepository.findByEmail(userData.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        var user = userMapper.map(userData);
        // хешируем пароль ПОСЛЕ маппинга, чтобы маппер не затер его !!
        user.setPassword(passwordEncoder.encode(userData.getPassword()));
        userRepository.save(user);
        return userMapper.map(user);
    }

    @Transactional
    public UserDTO update(UserDTO userData, Long id, String currentEmail) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Проверка прав. Обновить можно только себя
        if (!user.getEmail().equals(currentEmail)) {
            throw new AccessDeniedException("You can only update your own profile");
        }

        // Маппим остальные поля (firstName, lastName, email)
        userMapper.update(userData, user);

        // Отдельно обрабатываем пароль
        if (userData.getPassword() != null && !userData.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userData.getPassword()));
        }

        userRepository.save(user);
        return userMapper.map(user);
    }

    @Transactional
    public void delete(Long id, String currentEmail) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // удалять разрешено только себя
        if (!user.getEmail().equals(currentEmail)) {
            throw new AccessDeniedException("You can only delete your own profile");
        }

        userRepository.deleteById(id);
    }
}
