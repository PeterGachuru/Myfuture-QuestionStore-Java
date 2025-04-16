package ke.co.myfuture.Myfuture.UserManagement.Post.Postatempt;

import lombok.Data;

import java.util.List;

@Data
public class ArrayPostattemptRequest {
    Long installId;
    List<PostattemptRequest> attempts;
}
