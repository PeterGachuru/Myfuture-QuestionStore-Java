package ke.co.myfuture.Myfuture.Commonauth.Auth.UserPasswords;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String password;
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    @NotNull
    private Boolean isExpired = false;
    @ManyToOne(optional = false)
    @ToString.Exclude
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
