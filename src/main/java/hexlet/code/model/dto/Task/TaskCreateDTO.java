package hexlet.code.model.dto.Task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TaskCreateDTO {
    @NotBlank
    private String title;
    private Long index;
    @NotBlank
    private String status; // Slug статуса
    private String content;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    @JsonProperty("taskLabelIds")
    private Set<Long> labelIds;
}
