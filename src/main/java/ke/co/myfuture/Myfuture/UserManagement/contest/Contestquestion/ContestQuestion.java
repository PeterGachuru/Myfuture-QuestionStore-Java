package ke.co.myfuture.Myfuture.UserManagement.contest.Contestquestion;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
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
