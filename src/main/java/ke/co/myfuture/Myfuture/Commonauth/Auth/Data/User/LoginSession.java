package ke.co.myfuture.Myfuture.Commonauth.Auth.Data.User;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class LoginSession implements Serializable {
//    @Builder.Default
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Id
    private Long id = null;

    @Column(nullable = false)
    private Long userId = null;

    @Builder.Default
    private Boolean hasAcceptedTerms = false;

    @Builder.Default
    private String email = null;
    @Builder.Default
    private String phoneNumber = null;

    @Builder.Default
    private Integer firstLogin = null;

    @Builder.Default
    private String firstName = null;

    @Builder.Default
    private String lastName = null;

    @Builder.Default
    @Transient
    private List<UserRoleData> roles = null;

    @Builder.Default
    private String token = null;
    @Builder.Default
    private String refreshToken = null;
    @Builder.Default
    private String refreshedBy = null;

    @CreationTimestamp
    Date createdAt;
    @UpdateTimestamp
    Date updatedAt;

    Date loggoutTime;
}
