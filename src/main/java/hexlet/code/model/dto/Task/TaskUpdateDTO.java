package hexlet.code.model.dto.Task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class TaskUpdateDTO {
    private JsonNullable<String> title;
    private JsonNullable<Long> index;
    private JsonNullable<String> status;
    private JsonNullable<String> content;
    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;
    @JsonProperty("taskLabelIds")
    private JsonNullable<Set<Long>> labelIds;
}
