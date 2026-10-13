package ke.co.myfuture.Myfuture.QuestionStore.PermissionRequest;

import ke.co.myfuture.Myfuture.QuestionStore.Users.Users;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Data
public class PermissionRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    Users requester;
    Long permission;
    Long approver;
    @CreationTimestamp
    Date daterequested;
    Date dateapproved;
    Boolean reviewed = false;
}