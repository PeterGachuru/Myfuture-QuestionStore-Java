package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
public class SubtopicContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    @Lob
    String content;
}
