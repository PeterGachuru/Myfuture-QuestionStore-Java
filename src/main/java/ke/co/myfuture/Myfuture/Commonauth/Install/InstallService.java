package ke.co.myfuture.Myfuture.Commonauth.Install;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginSession;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class InstallService {
    private final Install2Repository install2Repository;
    public void addAccountDetails(Install install, User user) {
        if (user == null) return;
        if (install.getId() == null || install.getId() <= 0)
            return;

        install.setAccountEmail(user.getEmail());
        install.setAccountId(user.getId());
        install.setAccountAddedAt(new Date());

        install2Repository.save(install);
    }

    public void addAccountDetails(Install install, LoginSession user) {
        if (user == null) return;
        if (install.getId() == null || install.getId() <= 0)
            return;

        install.setAccountEmail(user.getEmail());
        install.setAccountId(user.getId());
        install.setAccountAddedAt(new Date());

        install2Repository.save(install);
    }
}
