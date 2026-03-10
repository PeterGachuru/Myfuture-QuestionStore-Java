package ke.co.myfuture.Myfuture.Commonauth.Auth.UserRole;


import ke.co.myfuture.Myfuture.Commonauth.Auth.RoleConfig.RoleConfig;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@DynamicUpdate
@Entity
@Table(name = "user_role_config", uniqueConstraints = {
        @UniqueConstraint(name = "user_role_config", columnNames = {"id"})
})
public class UserRole implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user",
            referencedColumnName = "id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "user_role_user"))
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role",
            referencedColumnName = "id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "user_role_role"))
    private RoleConfig role;

    @Column(name = "status")
    private Integer status;

    @CreationTimestamp
    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "creation_date", nullable = false)
    private Timestamp creation_date;

    @UpdateTimestamp
    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "update_date", nullable = false)
    private Timestamp update_date;
}
