package ke.co.myfuture.Myfuture.Commonauth.RememberMeToken;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "remember_me_tokens")
@Data
public class RememberMeToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long studentId;

    @Column(unique = true, nullable = false)
    private String token;

    private Date expiryDate;

    private Date createdAt = new Date();

    // getters and setters
}
