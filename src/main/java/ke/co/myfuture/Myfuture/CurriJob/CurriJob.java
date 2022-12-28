package ke.co.myfuture.Myfuture.CurriJob;

import ke.co.myfuture.Myfuture.CurriNormalChoice.CurriNormalChoice;
import ke.co.myfuture.Myfuture.CurriQuestion.CurriQuestion;
import ke.co.myfuture.Myfuture.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.Users.Users;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class CurriJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "partner")
    Users user;

    Long totalamount = 0L;
    Long totalpaid = 0L;

    String paymentmode;

    @ManyToOne
    @JoinColumn(name = "topic")
    CurriTopic topic;
    Boolean approveRequested  = false;

    Date dateRequested;
    Date dateSubmitted;
    Date dateApproved;

    Date lastcorrection;

    @ManyToOne
    @JoinColumn(name = "approver")
    Users approver;
    @OneToMany(mappedBy = "curriJob")
    List<CurriQuestion> questions;
}