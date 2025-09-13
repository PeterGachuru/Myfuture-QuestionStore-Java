package ke.co.myfuture.Myfuture.Commonauth.Auth.User;


import ke.co.myfuture.Myfuture.Commonauth.Auth.UserPasswords.UserPassword;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@DynamicUpdate
@Entity
//@Table(name = "user_config", uniqueConstraints = {
//        @UniqueConstraint(name = "user_id", columnNames = {"id"}),
//        @UniqueConstraint(name = "user_email", columnNames = {"email"}),
//})

public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name="email", unique = true)
    private String email;

    private String phoneNumber;

    @Column(name="first_name")
    private String firstName;
    private String county;

    @Column(name="last_name")
    private String lastName;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserPassword> passwords;

    @Column(name = "status")
    private String status;
    private String fullName;

    @Lob
    private String pictureUrl;

    @Column(name = "first_login")
    private Integer firstLogin;

    private Boolean hasAcceptedTerms = false;

    @CreationTimestamp
    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "creation_date", nullable = false)
    private Timestamp creationDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "update_date", nullable = false)
    private Timestamp updateDate;

    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "delete_date")
    private Timestamp deletedDate;

    @Column(name = "login_status")
    private Integer isLoggedIn = 0;

    private Long installId;

    @JsonIgnore
    private Timestamp lastLogin = new Timestamp(System.currentTimeMillis());

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expire")
    private Timestamp resetPasswordTokenExpire;

}
