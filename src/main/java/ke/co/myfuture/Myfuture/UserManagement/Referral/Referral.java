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

    Long newUserId;

    String newUserEmail;

    String referrerEmail;
    Long referrerStudentId;


    @Enumerated(EnumType.STRING)
    ReferralAction referralAction;

    @CreationTimestamp
    Date createdAt;
}