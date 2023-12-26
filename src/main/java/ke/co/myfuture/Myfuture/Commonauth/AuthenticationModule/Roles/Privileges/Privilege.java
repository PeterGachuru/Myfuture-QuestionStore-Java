package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Roles.Privileges;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Roles.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter @Setter
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
    private boolean selected;
    private String code;
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "roleFk")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Role role;

}
