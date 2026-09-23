package ke.co.myfuture.Myfuture.Commonauth.Utils;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.UserRequestContext;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Date;

@Embeddable
public class TransactionalStatus {
    @Column(nullable = false)
    Boolean posted = false;
    Date postedAt;
    String postedBy;

    @Column(nullable = false)
    Boolean verified = false;
    Date verifiedAt;
    String verifiedBy;

    public void post() {
        posted = true;
        Date now = new Date();
        this.postedAt = now;
        this.postedBy = UserRequestContext.getCurrentUserName();
        if (UserRequestContext.getCurrentUserName() == null)
            this.postedBy = "Internal";
    }
}
