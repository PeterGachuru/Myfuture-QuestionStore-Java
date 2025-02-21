package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TopicDto {
    private Long id;
//    @NotNull
    private String name;

//    @NotNull
    private Integer order;

//    @NotNull
    private Long subject;
    private Long parent;

//    @NotNull
    private Long curriLevel;
}
