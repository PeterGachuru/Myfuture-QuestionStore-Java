package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;


import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.UserData;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse implements Serializable {
    @Builder.Default
    private Integer status = HttpStatus.NOT_FOUND.value();

    @Builder.Default
    private String message = HttpStatus.NOT_FOUND.getReasonPhrase();

    @Builder.Default
    private UserData user = null;
}
