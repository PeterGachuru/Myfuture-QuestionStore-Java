package ke.co.myfuture.Myfuture.Commonauth.Auth.UserPasswords;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPasswordService {
    private final UserPasswordRepo userPasswordRepo;
    private final UserRepository userRepository;

    // runs at 0000 am every day
//    @Scheduled(cron = "0 0 0 * * ?")
    private void autoPasswordExpiry(){

        log.info("Updating passwords");
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());

        List<User> users = userRepository.findAll();

        users.forEach(user -> {
            user.getPasswords().forEach(userPassword -> {

                Calendar lastLog = Calendar.getInstance();
                lastLog.setTime(user.getLastLogin());
                lastLog.add(Calendar.DATE, 29);

                if (lastLog.before(today)){
                    user.setStatus("Locked");
                }else{
                    if (!userPassword.getIsExpired()){
                        Calendar passwordDate = Calendar.getInstance();
                        passwordDate.setTime(userPassword.getTimestamp());

                        passwordDate.add(Calendar.DATE, 59);
                        if (passwordDate.before(today)){
                            userPassword.setIsExpired(true);
                            user.setFirstLogin(1);
                        }
                    }
                }
            });
        });

        userRepository.saveAll(users);
    }
}
