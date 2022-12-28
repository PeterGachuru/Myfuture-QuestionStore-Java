package ke.co.myfuture.Myfuture.Requests;

import lombok.Data;

import javax.persistence.*;

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