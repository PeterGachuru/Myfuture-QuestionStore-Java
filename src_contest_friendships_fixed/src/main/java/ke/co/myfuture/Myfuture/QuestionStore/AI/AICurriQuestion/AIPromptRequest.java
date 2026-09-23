package ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriQuestion;


import lombok.Data;

@Data
public class AIPromptRequest {
    Long subtopicId;
    Long topicId;
    Long subjectId;
    Long levelId;
    String model;
}
