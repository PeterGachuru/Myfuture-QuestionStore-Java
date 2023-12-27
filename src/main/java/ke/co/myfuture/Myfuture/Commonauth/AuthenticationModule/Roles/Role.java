package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Roles;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Roles.Privileges.Privilege;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false, unique = true)
    private String name;
    @OneToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY, mappedBy = "role")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Privilege> privileges;

    //*****************Operational Audit *********************
    @Column(length = 30, nullable = false)
    @JsonIgnore
    private String postedBy;
    @Column(nullable = false)
    @JsonIgnore
    private Character postedFlag = 'Y';
//    @Column(nullable = false)
    @JsonIgnore
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
    @JsonIgnore
    private Character deletedFlag = 'N';
    @JsonIgnore
    private Date deletedTime;
}