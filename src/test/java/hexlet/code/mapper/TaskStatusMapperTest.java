package hexlet.code.mapper;

import hexlet.code.BaseTest;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.dto.TaskStatus.TaskStatusUpdateDTO;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TaskStatusMapperTest extends BaseTest {

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Test
    public void testTaskStatusMapperDetailed() {
        TaskStatus status = new TaskStatus();
        status.setName("OldName");
        status.setSlug("old_slug");

        // Update
        var updateDto = new TaskStatusUpdateDTO();
        updateDto.setName(JsonNullable.of("NewStatusName"));

        taskStatusMapper.update(updateDto, status);
        assertThat(status.getName()).isEqualTo("NewStatusName");
        assertThat(status.getSlug()).isEqualTo("old_slug"); // Не изменился
    }
}
