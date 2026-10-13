package ke.co.myfuture.Myfuture.UserManagement.Contest.Contestquestion;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
@Table(name = "contest_question", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"contest", "question"})
})
public class ContestQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    Long contest;
    @Column(nullable = false)
    Long question;
}
