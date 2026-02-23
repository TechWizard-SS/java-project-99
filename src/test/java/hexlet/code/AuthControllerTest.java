package hexlet.code;

import hexlet.code.model.User;
import hexlet.code.util.NamedRoutes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthControllerTest extends BaseTest {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        var user = userRepository.findByEmail("hexlet1@example.com")
                .orElseGet(() -> {
                    var newUser = new User();
                    newUser.setEmail("hexlet1@example.com");
                    newUser.setPassword(passwordEncoder.encode("password"));
                    newUser.setFirstName("Admin");
                    newUser.setLastName("Hexlet");
                    return userRepository.save(newUser);
                });
        token = getAuthToken(user);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        var data = Map.of(
                "username", "hexlet1@example.com",
                "password", "password"
        );

        var response = mockMvc.perform(post(NamedRoutes.API + NamedRoutes.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        String token = response.getContentAsString();
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.").length).isEqualTo(3);
    }

    @Test
    public void testLoginFailure() throws Exception {
        var data = Map.of(
                "username", "hexlet1@example.com",
                "password", "wrong_password"
        );

        mockMvc.perform(post(NamedRoutes.API + NamedRoutes.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isUnauthorized());
    }
}
