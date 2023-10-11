package ke.co.myfuture.Myfuture.QuestionStore.UserPermission;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"permission", "partner"})})
public class UserPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    Long permission;
    Long partner;
    Long requestId;
    @CreationTimestamp
    public Date createdAt;
}