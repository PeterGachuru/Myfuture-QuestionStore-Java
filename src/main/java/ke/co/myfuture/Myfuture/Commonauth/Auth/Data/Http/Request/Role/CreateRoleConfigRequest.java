package ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Request.Role;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Role.AccessRight;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoleConfigRequest implements Serializable {
    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "privileges")
    private List<AccessRight> accessRights;
}
