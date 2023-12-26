package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Users;


import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Roles.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sn", updatable = false)
    private Long sn;
    @Column(length = 6, nullable = false)
    private String entityId ="001";
    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;
    @Column(name = "firstname", length = 50, nullable = false)
    private String firstName;
    @Column(name = "lastname", length = 50, nullable = false)
    private String lastName;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;
    @Column(name = "phone", length = 50, nullable = false, unique = true)
    private String phoneNo;
    @Column(length = 50, unique = true)
    private String memberCode;

    @Column(name = "password", length = 255, nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Character isSystemGenPassword = 'Y';
    @Column(name = "sol_code", length = 10)
    private String solCode;
    @Column(name = "active")
    private Boolean isAcctActive = true;
    @Column(name = "first_login", nullable = false, length = 1)
    private Character firstLogin;
    @Column(name = "locked")
    public Boolean isAcctLocked;
    private String isTeller = "No";
    private String tellerAccount;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    private Long workclassFk;
    //*****************Operational Audit *********************
    @Column(length = 30, nullable = false)
    @JsonIgnore
    private String postedBy;
    @Column(nullable = false)
    @JsonIgnore
    private Character postedFlag = 'Y';
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date postedTime;
    @JsonIgnore
    private String modifiedBy;
    @JsonIgnore
    private Character modifiedFlag = 'N';
    @JsonIgnore
    private Date modifiedTime;
    @JsonIgnore
    private String verifiedBy;
    @JsonIgnore
    private Character verifiedFlag = 'N';
    @JsonIgnore
    private Date verifiedTime;
    @JsonIgnore
    private String deletedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character deletedFlag = 'N';
    @JsonIgnore
    private Date deletedTime;
}
