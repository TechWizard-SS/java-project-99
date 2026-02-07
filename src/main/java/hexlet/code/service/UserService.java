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

/**
 * Service class for managing users.
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Retrieves all users from the repository.
     *
     * @return list of user DTOs
     */
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::map)
                .toList();
    }

    /**
     * Finds a user by ID.
     *
     * @param id the user ID
     * @return user DTO
     * @throws ResourceNotFoundException if user is not found
     */
    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.map(user);
    }

    /**
     * Creates a new user.
     *
     * @param userData the user data to create
     * @return created user DTO
     * @throws RuntimeException if email already exists
     */
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

    /**
     * Updates an existing user.
     *
     * @param userData     the user data to update
     * @param id           the user ID
     * @param currentEmail the email of the currently authenticated user
     * @return updated user DTO
     * @throws ResourceNotFoundException if user is not found
     * @throws AccessDeniedException     if user tries to update another user's profile
     */
    @Transactional
    public UserDTO update(UserDTO userData, Long id, String currentEmail) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        System.out.println("Before update: " + user.getFirstName());

        userMapper.update(userData, user);

        System.out.println("After mapper: " + user.getFirstName());

        if (userData.getPassword() != null && !userData.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userData.getPassword()));
        }

        var savedUser = userRepository.save(user);
        userRepository.flush(); // Принудительно отправляем в базу ПРЯМО СЕЙЧАС

        return userMapper.map(savedUser);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id           the user ID
     * @param currentEmail the email of the currently authenticated user
     * @throws ResourceNotFoundException if user is not found
     * @throws AccessDeniedException     if user tries to delete another user's profile
     */
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
