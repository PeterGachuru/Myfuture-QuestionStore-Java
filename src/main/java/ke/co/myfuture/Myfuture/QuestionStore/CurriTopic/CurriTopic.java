package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class CurriTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "parent")
    CurriTopic parent;
//    @OneToMany(mappedBy = "parent")
//    @ToString.Exclude
//    List<CurriTopic> children;
    @Column(nullable = false)
    String name;
    @ManyToOne
    @JoinColumn(name = "curri_level")
    CurriLevel curriLevel;
    @ManyToOne
    @JoinColumn(name = "subject")
    Subject subject;

//    @OneToOne(cascade = CascadeType.PERSIST)
//    SubtopicContent subtopicContent;

    @Lob
    String content;

    Boolean deleted = false;
    Boolean required = true;

    @Embedded
    AuditTrails auditTrails = new AuditTrails();
}