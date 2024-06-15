package ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.User;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.User.LoginData;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse implements Serializable {
    @Builder.Default
    private Integer statusCode = HttpStatus.NOT_FOUND.value();

    @Builder.Default
    private String message = HttpStatus.NOT_FOUND.getReasonPhrase();

    @Builder.Default
    private LoginData user = null;
}
