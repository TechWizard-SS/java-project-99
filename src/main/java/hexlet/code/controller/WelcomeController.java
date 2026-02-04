package hexlet.code.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for welcome page.
 */
@RestController
public final class WelcomeController {

    /**
     * Returns welcome message.
     *
     * @return welcome message string
     */
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Spring";
    }
}
