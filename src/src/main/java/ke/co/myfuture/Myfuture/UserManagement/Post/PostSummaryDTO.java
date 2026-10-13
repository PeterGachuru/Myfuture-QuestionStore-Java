package ke.co.myfuture.Myfuture.UserManagement.Post;

import java.util.Date;

public class PostSummaryDTO {
    public Long id;
    public String senderName;
    public Long questionId;
    public Long inid;
    public Long installId;
    public Date createdAt;

    public PostSummaryDTO(Post post) {
        this.id = post.id;
        this.senderName = post.studentaccount != null ? post.studentaccount.getName() : "N/A";
        this.questionId = post.questionid;
        this.inid = post.inid;
        this.installId = post.installId;
        this.createdAt = post.createdAt;
    }
}
