package hexlet.code.model.dto.Task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private Long index;
    private String title;
    private String content;
    private String status;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    @JsonProperty("taskLabelIds")
    private Set<Long> labelIds;
    private Instant createdAt;
}
