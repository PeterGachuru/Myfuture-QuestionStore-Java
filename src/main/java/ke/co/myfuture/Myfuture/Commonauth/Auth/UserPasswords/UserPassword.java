package ke.co.myfuture.Myfuture.Commonauth.Auth.UserPasswords;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String password;
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private Boolean isExpired = false;
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
