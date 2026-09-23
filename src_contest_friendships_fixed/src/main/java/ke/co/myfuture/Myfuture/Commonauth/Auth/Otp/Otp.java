package ke.co.myfuture.Myfuture.Commonauth.Auth.Otp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String otpValue;
    private String email;
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    @Lob
    private String jwt = null;
    private Boolean isValid;
    private Integer retries = 0;
}
