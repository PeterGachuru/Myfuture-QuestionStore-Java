package ke.co.myfuture.Myfuture.UserManagement.Post.Postatempt;

import lombok.Data;

@Data
public class PostattemptRequest {
    Boolean scored;
    Long postId;
    Long selectedChoice;
    Long studentId;
}
