package hexlet.code.mapper;

import hexlet.code.BaseTest;
import hexlet.code.model.User;
import hexlet.code.model.dto.User.UserCreateDTO;
import hexlet.code.model.dto.User.UserUpdateDTO;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserMapperTest extends BaseTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUserMapperDetailed() {
        // Entity -> DTO
        User user = new User();
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("test@test.com");
        user.setPassword("secret");

        var dto = userMapper.map(user);
        assertThat(dto.getFirstName()).isEqualTo("First");
        // Проверяем, что в DTO нет пароля

        // CreateDTO -> Entity
        var createDto = new UserCreateDTO();
        createDto.setFirstName("New");
        createDto.setLastName("User");
        createDto.setEmail("new@test.com");
        createDto.setPassword("12345");

        User newUser = userMapper.map(createDto);
        assertThat(newUser.getPassword()).isEqualTo("12345");

        // UpdateDTO -> Entity
        var updateDto = new UserUpdateDTO();
        updateDto.setFirstName(JsonNullable.of("UpdatedName"));
        updateDto.setPassword(JsonNullable.of("NewPass")); // Это поле должно игнорироваться

        userMapper.update(updateDto, user);
        assertThat(user.getFirstName()).isEqualTo("UpdatedName");
        assertThat(user.getPassword()).isEqualTo("secret"); // Остался старый
    }
}
