package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;


import lombok.*;

import java.io.Serializable;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRoleRequest implements Serializable {
    private Long roleId;
    private  String email;
}
