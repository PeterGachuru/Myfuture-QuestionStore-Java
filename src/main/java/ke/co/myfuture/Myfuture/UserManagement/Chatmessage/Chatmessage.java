package ke.co.myfuture.Myfuture.UserManagement.Chatmessage;

import ke.co.myfuture.Myfuture.Commonauth.Install.Install;
import ke.co.myfuture.Myfuture.UserManagement.Cgroup.Cgroup;
import ke.co.myfuture.Myfuture.UserManagement.Sender.Sender;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"inid", "installId"})})
public class Chatmessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(nullable = false)
    Long inid;
    @Column(nullable = false)
    String message;
    @ManyToOne(targetEntity = Cgroup.class)
    @JoinColumn(name = "groupid",  nullable = false)
    Cgroup cgroup;
    @Column(nullable = false)
    Long installId;
    @ManyToOne(targetEntity = Sender.class)
    @JoinColumn(name = "sender", nullable = false)
    Sender sender;
    String replyToMessage;
    @ManyToOne(targetEntity = Chatmessage.class)
    Chatmessage replyingTo;
    Long replyToId;
    boolean beenRead = false;
    @CreationTimestamp
    Date createdAt;
    @UpdateTimestamp
    Date updatedAt;
}