package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.Commonauth.Utils.AuditTrails;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    Integer numbering;

//    @OneToOne(cascade = CascadeType.PERSIST)
//    SubtopicContent subtopicContent;

    @Lob
    String content;

    String instructionsOnGenerationOfQuestions;
    String instructionsOnGenerationOfNotes;

    Integer percentageOfRejectedQuestions;
    Integer totalNumberOfApprovedQuestions;
    Integer totalNumberOfUnverifiedQuestions;

    Boolean isParent = false;
    Boolean deleted = false;
    Boolean required = true;

    @Transient
    List<CurriTopic> children = new ArrayList<>();


    //    @UpdateTimestamp
    Date updatedAt;

//    @CreationTimestamp

    @Column(updatable = false)
    Date createdAt;

    Date deletedAt;


    @Column(nullable = false)
    String createdBy;
    String deletedBy;

    public void delete() {
        this.deletedAt = new Date();
        this.deleted = true;
        this.deletedBy = UserRequestContext.getCurrentUserName();
    }

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
        this.createdBy = UserRequestContext.getCurrentUserName();
        if (UserRequestContext.getCurrentUserName() == null)
            this.createdBy = "Internal";
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    public void update(CurriTopic topic) {
        this.name = topic.name;
        this.content = topic.content;
        this.instructionsOnGenerationOfQuestions = topic.instructionsOnGenerationOfQuestions;
        this.instructionsOnGenerationOfNotes = topic.instructionsOnGenerationOfNotes;
    }
}