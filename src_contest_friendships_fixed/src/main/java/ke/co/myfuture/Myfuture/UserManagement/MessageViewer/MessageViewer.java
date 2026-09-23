package ke.co.myfuture.Myfuture.UserManagement.MessageViewer;

import ke.co.myfuture.Myfuture.UserManagement.Chatmessage.Chatmessage;
import ke.co.myfuture.Myfuture.UserManagement.Sender.Sender;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"inid", "install"}),
                @UniqueConstraint(columnNames = {"message", "viewer"})})
public class MessageViewer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @OneToOne(targetEntity = Sender.class)
    @JoinColumn(name = "viewer", referencedColumnName = "id")
    Sender viewer;
    @OneToOne(targetEntity = Chatmessage.class)
    @JoinColumn(name = "message", referencedColumnName = "id")
    Chatmessage message;
    @Column(nullable = false)
    Long install;
    @Column(nullable = false)
    Long inid;
    @CreationTimestamp
    Date viewedAt;
}