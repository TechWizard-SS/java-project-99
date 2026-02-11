package hexlet.code.controller;

import hexlet.code.model.dto.UserDTO;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public final class UserController {

    @Autowired
    private UserService userService;

    // Получение списка всех пользователей
    @GetMapping("")
    public ResponseEntity<List<UserDTO>> index() {
        List<UserDTO> users = userService.getAll();
        return ResponseEntity.ok()
                // Добавляем обязательный заголовок для React Admin
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    // Получение конкретного пользователя по ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO show(@PathVariable Long id) {
        return userService.findById(id);
    }

    // Создание пользователя
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserDTO userData) {
        return userService.create(userData);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@RequestBody UserDTO userData,
                          @PathVariable Long id,
                          @AuthenticationPrincipal UserDetails currentUser) {

        if (currentUser == null) {
             throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
            //String fakeEmail = "admin@example.com"; // для тестирования!
            //return userService.update(userData, id, fakeEmail);
        }
        return userService.update(userData, id, currentUser.getUsername());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void destroy(@PathVariable Long id,
                        @AuthenticationPrincipal UserDetails currentUser) {

        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
            //String fakeEmail = "admin@example.com"; // Только для тестирования!
            //userService.delete(id, fakeEmail);
            //return;
        }
        userService.delete(id, currentUser.getUsername());
    }
}
