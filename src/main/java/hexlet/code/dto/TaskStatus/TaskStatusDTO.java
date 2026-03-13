package hexlet.code.dto.TaskStatus;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@EqualsAndHashCode
public class TaskStatusDTO {
    private Long id;
    private String name;
    private String slug;
    private Instant createdAt;
}
