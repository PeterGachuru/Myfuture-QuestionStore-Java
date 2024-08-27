package ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice.CurriNormalChoice;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.Utils.Response.StaticFunctionUtils;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import static ke.co.myfuture.Myfuture.Utils.Response.StaticFunctionUtils.simpleDateFormat;

@Entity
@Data
public class CurriQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    String string;

    @Lob
    String explanation;

    @Column(nullable = false)
    String bookModel;

    Long job;
    Long curriJob;
    @ManyToOne
    @JoinColumn(name = "subtopic")
    CurriTopic subtopic;

    @CreationTimestamp
    Date createdAt;
//    public String getCreatedAt() {
//        return StaticFunctionUtils.simpleDateFormat(createdAt);
//    }
    Date approverRequestDate;
//    public String getApproverRequestDate() {
//        return StaticFunctionUtils.simpleDateFormat(approverRequestDate);
//    }
    @UpdateTimestamp
    Date updatedAt;
//    public String getUpdatedAt() {
//        return StaticFunctionUtils.simpleDateFormat(updatedAt);
//    }
    @Column(nullable = false)
    Integer imageLevel = 1;


    @Column(unique = true)
    Long cgroup;
    Integer editionNumber = 1;
    @Column(nullable = false)
    Long updateId ;
    Boolean hasImage;
    Boolean choicesWithImages;
    String imageCode;

    Integer imageHeight;
    Integer imageWidth;

    @OneToMany(mappedBy =  "question")
//    @Transient
    List<CurriNormalChoice> choices;

    @Column(nullable = false)
    Boolean sharable = false;
    @Column(nullable = false)
    Boolean reviewed = false;
    Boolean deleted = false;
    Date approvalDate;
    Date deletionDate;
    String approvedBy;
    String deletedBy;
//    public String getApprovalDate() {
//        return StaticFunctionUtils.simpleDateFormat(approvalDate);
//    }

    @PrePersist
    void init() {
        deleted = false;
    }

    public void delete() {
        deleted = true;
        sharable = false;
        deletionDate = new Date();
        reviewed = true;
        deletedBy =  UserRequestContext.getCurrentUserName();
    }
    public void approve() {
        sharable = true;
        approvalDate = new Date();
        deleted = false;
        reviewed = true;
        approvedBy = UserRequestContext.getCurrentUserName();
    }
}
