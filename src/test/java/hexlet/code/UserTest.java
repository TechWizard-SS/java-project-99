package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.NamedRoutes;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

///**
// * Test class for User controller.
// */
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//@WithMockUser(username = "hexlet@example.com")
//public class UserTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ObjectMapper om;
//
//    private User testUser;
//
//    /**
//     * Setup method for tests.
//     */
//
//    @BeforeEach
//    public void setUp() {
//        userRepository.deleteAll();
//
//        testUser = Instancio.of(User.class)
//                .ignore(Select.field(User::getId))
//                .set(Select.field(User::getEmail), "hexlet@example.com")
//                .ignore(Select.field(User::getCreatedAt))
//                .ignore(Select.field(User::getUpdatedAt))
//                .create();
//        userRepository.save(testUser);
//    }
//
//    @Test
//    public void testIndex() throws Exception {
//        mockMvc.perform(get(NamedRoutes.getUsers()))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testShow() throws Exception {
//        mockMvc.perform(get(NamedRoutes.userGetId(testUser.getId())))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testCreate() throws Exception {
//        var data = new HashMap<String, String>();
//        data.put("firstName", "Jane");
//        data.put("email", "new-unique-email@example.com");
//        data.put("password", "superpass");
//
//        mockMvc.perform(post(NamedRoutes.userPost())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(om.writeValueAsString(data)))
//                .andExpect(status().isCreated());
//    }
//
//    @Test
//    public void testUpdate() throws Exception {
//        var data = new HashMap<String, String>();
//        data.put("firstName", "NewName");
//        data.put("email", "user@example.com");
//        data.put("password", "newPass123");
//
//        mockMvc.perform(put(NamedRoutes.userUpdate(testUser.getId()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(om.writeValueAsString(data)))
//                .andExpect(status().isOk());
//
//        var user = userRepository.findById(testUser.getId()).get();
//        assertThat(user.getFirstName()).isEqualTo("NewName");
//    }
//
//    @Test
//    public void testDelete() throws Exception {
//        mockMvc.perform(delete(NamedRoutes.userDelete(testUser.getId())))
//                .andExpect(status().isOk());
//
//        assertThat(userRepository.existsById(testUser.getId())).isFalse();
//    }
//}
