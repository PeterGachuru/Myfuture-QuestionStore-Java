package ke.co.myfuture.Myfuture.QuestionStore.Writersbroadcast;

import ke.co.myfuture.Myfuture.NonJdbc.Migration.MigratorService;
import ke.co.myfuture.Myfuture.QuestionStore.Users.WriterUsersRepository;
import ke.co.myfuture.Myfuture.UserManagement.MailService.MailService;
import ke.co.myfuture.Myfuture.UserManagement.Useraccount.UserAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class WritersbroadcastService {
    @Autowired
    WritersbroadcastRepository writersbroadcastRepository;
    @Autowired
    MailService mailService;

    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    WriterUsersRepository writerUsersRepository;

    @Autowired
    BroadcastSentToRepo broadcastSentToRepo;

    @Autowired
    MigratorService migratorService;
//    @Scheduled(fixedRate = 3600000L, initialDelay = 0)
    @Bean
    public void broadCastToStudents() {
        System.out.println("public void broadCastToStudents() {");
        List<Writersbroadcast> writersbroadcastList = writersbroadcastRepository.findToSendForPupils();
        for (Writersbroadcast writersbroadcast: writersbroadcastList) {
            Boolean success = false;
            Set<String> pupilEmailList = getPupilEmailList();
            filterUnsent(pupilEmailList, writersbroadcast);
            pupilEmailList.add("ngangagachuru919@gmail.com");
            pupilEmailList.add("ngangagachuru001@gmail.com");
            List<String> nonDuplicateList = new ArrayList<>(pupilEmailList);
            writersbroadcast.setTargetCount(pupilEmailList.size());
            writersbroadcast.setDateSent(new Date());
            writersbroadcastRepository.save(writersbroadcast);
            System.out.println(writersbroadcast);
            int successSent = 0;
//            List<String> recipients = trimEmailList(writersbroadcast, pupilEmailList);
            List<String> recipients;
            while (!nonDuplicateList.isEmpty()) {
                recipients = new ArrayList<>();
                for (int i = 0; i < 100 && !nonDuplicateList.isEmpty(); i++) {
                    if (!nonDuplicateList.get(0).contains("example"))
                        recipients.add(nonDuplicateList.get(0));
                    nonDuplicateList.remove(0);
                }
                success = mailService.sendEmail("", writersbroadcast.getHtml(),  writersbroadcast.getSubject(), recipients);
                System.out.println(success);
                if (success) {
                    List<BroadcastSentTo> sentTo = new ArrayList<>();
                    for (String email: recipients) {
                        BroadcastSentTo broadcastSentTo = new BroadcastSentTo();
                        broadcastSentTo.setEmail(email);
                        broadcastSentTo.setWritersbroadcast(writersbroadcast);
                        sentTo.add(broadcastSentTo);
                    }
                    broadcastSentToRepo.saveAll(sentTo);
                }else {
                    break;
                }
            }

            if (success){
                writersbroadcast.setCountSentTo(successSent);
                writersbroadcast.setDateFinishedSending(new Date());
                writersbroadcastRepository.save(writersbroadcast);
            }
        }
        System.out.println("Finished sending email");
    }

    private Set<String> filterUnsent(Set<String> pupilEmailList, Writersbroadcast writersbroadcast) {
        Set<String> sentEmails = broadcastSentToRepo.getSent(writersbroadcast.getId());

        for (String exists: sentEmails) {
            while (pupilEmailList.contains(exists))
                pupilEmailList.remove(exists);
        }
        return pupilEmailList;
    }

    private Set<String> getPupilEmailList() {
        Set<String> emails = userAccountRepository.getEmails();
        emails.addAll(migratorService.queryStringArray("select distinct email from statistics where length(email) > 14 and email not like '%,%' and email like '%@%.%'  and email not like '% %'  and email not like '%.%.%'  and email not like '%.' and email not like '@%';"));
//        emails.addAll(migratorService.queryStringArray("select distinct email from statistics where email REGEXP '^[^@]+@[^@]+\\.[^@]{2,}$';"));
        return emails;
    }

//    @Scheduled(fixedRate = 3600000L, initialDelay = 0)
    @Bean
    public void broadCastToWriters() {
        System.out.println("public void broadCastToWriters() {");
        List<Writersbroadcast> writersbroadcastList = writersbroadcastRepository.findToSendForWriters();
        for (Writersbroadcast writersbroadcast: writersbroadcastList) {
            List<String> pupilEmailList = getWriterEmailList();
            writersbroadcast.setTargetCount(pupilEmailList.size());
            writersbroadcast.setDateSent(new Date());
            writersbroadcastRepository.save(writersbroadcast);
            int successSent = 0;
            Boolean success;
            for (String email: pupilEmailList){
//                success = mailService.sendEmail(email, writersbroadcast.getSubject(), writersbroadcast.getHtml());
//                if (success)
//                    successSent++;
//                break;
            }
            writersbroadcast.setCountSentTo(successSent);
            writersbroadcast.setDateFinishedSending(new Date());
            writersbroadcastRepository.save(writersbroadcast);
        }
    }

    private List<String> getWriterEmailList() {
        return writerUsersRepository.getEmails();
    }

    public void sendEmail(String email, String subject,  String message) {
//        List<String> recipients = new ArrayList<>();
//        recipients.addAll(email)
        mailService.sendEmail(email, message,  subject, new ArrayList<>());
    }
}
