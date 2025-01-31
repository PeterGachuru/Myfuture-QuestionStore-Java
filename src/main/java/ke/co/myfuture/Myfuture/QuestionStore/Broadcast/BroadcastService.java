package ke.co.myfuture.Myfuture.QuestionStore.Broadcast;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.NonJdbc.Migration.MigratorService;
import ke.co.myfuture.Myfuture.QuestionStore.Users.WriterUsersRepository;
import ke.co.myfuture.Myfuture.Commonauth.ScheduledLearnerEmails.SchedulerService;
import ke.co.myfuture.Myfuture.Commonauth.ScheduledLearnerEmails.SenderService;
import ke.co.myfuture.Myfuture.UserManagement.Useraccount.UserAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class BroadcastService {
    @Autowired
    BroadcastRepository broadcastRepository;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    WriterUsersRepository writerUsersRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    SchedulerService schedulerService;

    @Autowired
    MigratorService migratorService;
//    @Scheduled(fixedRate = 3600000L, initialDelay = 0)
//    @Bean
    public void broadCastToStudents() {
        System.out.println("public void broadCastToStudents() {");
        List<Broadcast> broadcastList = broadcastRepository.findToSendForPupils();
        for (Broadcast broadcast : broadcastList) {
            broadCastToStudents(broadcast);
        }
        System.out.println("Finished sending email");
    }

    public Boolean broadCastToStudents(Broadcast broadcast) {
        Set<String> pupilEmailList = getPupilEmailList();
        filterUnsent(pupilEmailList, broadcast);
        pupilEmailList.add("ngangagachuru919@gmail.com");
        pupilEmailList.add("ngangagachuru001@gmail.com");
        List<String> nonDuplicateList = new ArrayList<>(pupilEmailList);
//            nonDuplicateList.add(0,"ngangagachuru919@gmail.com");
//            nonDuplicateList.add(0,"ngangagachuru001@gmail.c]om");
        broadcast.setTargetCount(pupilEmailList.size());
        broadcast.setDateSent(new Date());
        broadcast.startSend();
        broadcastRepository.save(broadcast);
        System.out.println(broadcast);
        int successSent = 0;
//            List<String> recipients = trimEmailList(writersbroadcast, pupilEmailList);
        List<String> recipients;
        int count = 0;
        while (!nonDuplicateList.isEmpty()) {
            recipients = new ArrayList<>();
            for (int i = 0; i < 100 && !nonDuplicateList.isEmpty(); i++) {
                count++;
                if (!nonDuplicateList.get(0).contains("example")) {
                    schedulerService.persistScheduledEmail(nonDuplicateList.get(0), broadcast.getSubject(), broadcast.getHtml(), LocalDateTime.now().plusMinutes(count/5), SenderService.Broadcast);
                }
                nonDuplicateList.remove(0);
            }
        }

        broadcast.setCountSentTo(successSent);
        broadcast.setDateFinishedSending(new Date());
        broadcastRepository.save(broadcast);
        return true;
    }


    private Set<String> filterUnsent(Set<String> pupilEmailList, Broadcast broadcast) {
        Set<String> sentEmails = Set.of();

        for (String exists: sentEmails) {
            while (pupilEmailList.contains(exists))
                pupilEmailList.remove(exists);
        }
        return pupilEmailList;
    }

    private Set<String> getPupilEmailList() {
        Set<String> emails = userAccountRepository.getEmails();
        emails.addAll(userRepository.getAllEmailAddresses());
        emails.addAll(migratorService.queryStringArray("select distinct email from statistics where length(email) > 14 and email not like '%,%' and email like '%@%.%'  and email not like '% %'  and email not like '%.%.%'  and email not like '%.' and email not like '@%';"));
//        emails.addAll(migratorService.queryStringArray("select distinct email from statistics where email REGEXP '^[^@]+@[^@]+\\.[^@]{2,}$';"));
        return emails;
    }

//    @Scheduled(fixedRate = 3600000L, initialDelay = 0)
    @Bean
    public void broadCastToWriters() {
        System.out.println("public void broadCastToWriters() {");
        List<Broadcast> broadcastList = broadcastRepository.findToSendForWriters();
        for (Broadcast broadcast : broadcastList) {
            List<String> pupilEmailList = getWriterEmailList();
            broadcast.setTargetCount(pupilEmailList.size());
            broadcast.setDateSent(new Date());
            broadcastRepository.save(broadcast);
            int successSent = 0;
            Boolean success;
            for (String email: pupilEmailList){
//                success = mailService.sendEmail(email, writersbroadcast.getSubject(), writersbroadcast.getHtml());
//                if (success)
//                    successSent++;
//                break;
            }
            broadcast.setCountSentTo(successSent);
            broadcast.setDateFinishedSending(new Date());
            broadcastRepository.save(broadcast);
        }
    }

    private List<String> getWriterEmailList() {
        return writerUsersRepository.getEmails();
    }

    public void sendEmail(String email, String subject,  String message) {
//        List<String> recipients = new ArrayList<>();
//        recipients.addAll(email)

        schedulerService.persistScheduledEmail(email,  subject, message,  LocalDateTime.now(), SenderService.PersonalBroadcast);
    }
}
