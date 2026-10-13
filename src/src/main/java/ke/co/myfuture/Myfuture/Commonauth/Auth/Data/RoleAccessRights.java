package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;

import ke.co.myfuture.Myfuture.Commonauth.Auth.RoleConfig.AccessRight;
import lombok.*;

import java.io.Serializable;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleAccessRights implements Serializable {
    @Builder.Default
    private String name = null;

    @Builder.Default
    private AccessRight accessRights = null;

    @Builder.Default
    private String category = null;

    @Builder.Default
    private String subCategory = null;
}
