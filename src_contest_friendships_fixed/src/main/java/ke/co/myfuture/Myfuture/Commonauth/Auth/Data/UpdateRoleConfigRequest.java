package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;

import ke.co.myfuture.Myfuture.Commonauth.Auth.RoleConfig.AccessRight;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleConfigRequest implements Serializable {
    private Long id;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "privileges")
    private List<AccessRight> accessRights;
}
