package hexlet.code.dto.Task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
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
