package ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriQuestion;


import lombok.Data;

@Data
public class QuestionApprovalsResponse {
    String topic = "Test";
    String subtopic = "Test";
    Integer totalCount = 0;
    Integer approvedCount = 0;
    Integer rejectedCount = 0;
}
