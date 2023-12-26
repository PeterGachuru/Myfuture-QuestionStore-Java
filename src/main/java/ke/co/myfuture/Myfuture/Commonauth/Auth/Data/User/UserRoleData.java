package ke.co.myfuture.Myfuture.Commonauth.Auth.Data.User;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Role.RoleAccessRights;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoleData implements Serializable {
    @Builder.Default
    private String name = null;

    @Builder.Default
    private List<RoleAccessRights> accessRights = null;
}
