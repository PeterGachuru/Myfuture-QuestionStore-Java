package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Responses;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupResponse {
    private String message;
    private Instant timestamp;
}
