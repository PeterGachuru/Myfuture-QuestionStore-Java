package ke.co.myfuture.Myfuture.CurriJobapprove;

import ke.co.myfuture.Myfuture.CurriQuestionApprove.CurriQuestionApprove;
import ke.co.myfuture.Myfuture.Users.Users;

import javax.persistence.*;
import java.util.List;

public class CurriJobapprove {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;


    @ManyToOne
    @JoinColumn(name = "partner")
    Users user;

    @OneToMany
    @JoinColumn(referencedColumnName = "job")
    List<CurriQuestionApprove> questionApproves;
}