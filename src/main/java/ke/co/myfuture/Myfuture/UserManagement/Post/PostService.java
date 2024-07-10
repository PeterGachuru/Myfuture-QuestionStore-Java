package ke.co.myfuture.Myfuture.UserManagement.Post;


import ke.co.myfuture.Myfuture.UserManagement.Studentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.Studentaccount.StudentAccountRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    StudentAccountRepository studentAccountRepository;

    public List<Post> save(PostsHolder postsHolder) {
        System.out.println(postsHolder);
        List<Post> posted = new ArrayList<>();
        Optional<IbukaStudentAccount> ibukaStudentAccount;
        Post post, savedPost;
        for (PostsHolder.PostCreation postCreation: postsHolder.posts) {
            post = new Post();
            post.inid = postCreation.inid;
            ibukaStudentAccount =  studentAccountRepository.findById(postCreation.studentId);
            if (ibukaStudentAccount.isEmpty())
                continue;
            if (!Objects.equals(ibukaStudentAccount.get().getParent(), postsHolder.parentId))
                continue;
            post.studentaccount = ibukaStudentAccount.get();
            post.questionid = postCreation.questionid;
            post.installId = postsHolder.installId;

            savedPost = postRepository.save(post);
            posted.add(savedPost);
        }
        return posted;
    }


    @Data
    static public class  PostsHolder {
        Long parentId;
        Long installId;

        List<PostCreation> posts;

        @Data
        static public class PostCreation {
            Long studentId;
            Long inid;
            Long questionid;
        }
    }
}
