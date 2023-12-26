package ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.Role;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Role.RoleData;
import lombok.*;
import org.springframework.http.HttpStatus;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResponse {
    @Builder.Default
    private Integer status = HttpStatus.NOT_FOUND.value();

    @Builder.Default
    private String message = HttpStatus.NOT_FOUND.getReasonPhrase();

    @Builder.Default
    private RoleData role = null;
}
