package ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.User;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.User.UserData;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersResponse implements Serializable {
    @Builder.Default
    private Integer status = HttpStatus.NOT_FOUND.value();

    @Builder.Default
    private String message = HttpStatus.NOT_FOUND.getReasonPhrase();

    @Builder.Default
    private List<UserData> users = null;
}
