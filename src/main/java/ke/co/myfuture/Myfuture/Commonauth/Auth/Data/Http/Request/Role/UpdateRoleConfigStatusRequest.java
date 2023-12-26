package ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Request.Role;

import lombok.*;

import java.io.Serializable;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleConfigStatusRequest implements Serializable {
    private Long id;
}
