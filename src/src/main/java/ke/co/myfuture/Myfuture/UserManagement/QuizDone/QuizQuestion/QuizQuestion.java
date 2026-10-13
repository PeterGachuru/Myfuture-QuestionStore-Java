package ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizQuestion;

import lombok.Data;

import jakarta.persistence.*;


@Entity
@Data
@Table(name = "quiz_question", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"quiz", "questionId"})
})
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    public Long quiz;
    @Column(nullable = false)
    public Long questionId;
    @Column(nullable = false)
    public String choicesOrder;
    public Long selectedChoice;
    public Boolean gotCorrect;
//    @Column(nullable = false)
    public Integer position;
}
