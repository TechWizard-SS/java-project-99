package hexlet.code.controller;

import hexlet.code.model.dto.UserDTO;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Получение списка всех пользователей
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> index() {
        return userService.getAll();
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
    public UserDTO update(@RequestBody UserDTO userData,
                          @PathVariable Long id,
                          @AuthenticationPrincipal UserDetails currentUser) {
        return userService.update(userData, id, currentUser.getUsername());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void destroy(@PathVariable Long id, Principal principal) {
        // principal.getName() вернет email текущего залогиненного пользователя
        userService.delete(id, principal.getName());
    }
}
