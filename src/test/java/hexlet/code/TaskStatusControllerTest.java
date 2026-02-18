package hexlet.code;

import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import hexlet.code.util.NamedRoutes;
import java.util.Map;

@SpringBootTest
public class TaskStatusControllerTest extends BaseTest {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Test
    public void testCreateStatusWithDuplicateSlug() throws Exception {
        var status = new TaskStatus();
        status.setName("Existing");
        status.setSlug("existing_slug");
        taskStatusRepository.save(status);

        var data = Map.of("name", "New", "slug", "existing_slug");

        mockMvc.perform(post(NamedRoutes.TASK_STATUSES)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isNotFound()); // Твой сервис кинет ResourceNotFoundException ("Slug already exists")
    }
}
