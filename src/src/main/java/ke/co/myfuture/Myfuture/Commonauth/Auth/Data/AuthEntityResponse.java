package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthEntityResponse implements Serializable {
    @Builder.Default
    private Integer statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();

    @Builder.Default
    private String message = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
}
