package ke.co.myfuture.Myfuture.QuestionStore.CurriNotes;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class CurriNotes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Lob
    String content;

    @ManyToOne
    @JoinColumn(name = "subtopic")
    CurriTopic subtopic;

    @Column(unique = true)
    Long cgroup;

    @Column(nullable = false)
    String bookModel;

    @Embedded()
    AuditTrails auditTrails = new AuditTrails();

    @Transient
    AuditTrails.Retriever audits;

    public void update(CurriNotes bookFromUser) {
        content = bookFromUser.content;
    }
}
