package ke.co.myfuture.Myfuture.QuestionStore.Requests;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
public class Requests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    String clientphone;
    Long requested;
    String approverphone;
    Long daterequested;
    Long dateapproved;
    String handled;
}