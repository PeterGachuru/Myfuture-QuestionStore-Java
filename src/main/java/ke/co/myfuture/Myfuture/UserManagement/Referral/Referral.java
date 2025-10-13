package ke.co.myfuture.Myfuture.UserManagement.Referral;


import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"newUserId", "referrerCode"})})
public class Referral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    public Long installId;

    @Column(nullable = false)
    String referrerCode;

    @Column(nullable = false)
    Long newUserId;

    @Column(nullable = false)
    String newUserEmail;

    String referrerEmail;
    Long referrerStudentId;

    @CreationTimestamp
    Date createdAt;
}