package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

public interface CurriTopicView {
    Long getId();
    Long getParent();
    String getName();
    Long getCurriLevel();
    Long getSubject();
    java.sql.Timestamp getCreatedAt();
    Integer getNumbering();
    Boolean getDeleted();
    Boolean getRequired();
    java.sql.Timestamp getUpdatedAt();
    String getContent();
    String getSlug();
    String getCreatedBy();
    Boolean getDeletedFlag();
    java.util.Date getDeletedAt();
    String getDeletedBy();
    String getInstructionsOnGenerationOfNotes();
    String getInstructionsOnGenerationOfQuestions();
    Integer getPercentageOfRejectedQuestions();
    Integer getTotalNumberOfApprovedQuestions();
    Integer getTotalNumberOfUnverifiedQuestions();
    Boolean getIsParent();
}