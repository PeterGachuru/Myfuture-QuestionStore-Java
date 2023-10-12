package ke.co.myfuture.Myfuture.QuestionStore.Writersbroadcast;

import ke.co.myfuture.Myfuture.NonJdbc.MigratorService;
import ke.co.myfuture.Myfuture.QuestionStore.Users.WriterUsersRepository;
import ke.co.myfuture.Myfuture.UserManagement.MailService.MailService;
import ke.co.myfuture.Myfuture.UserManagement.Useraccount.UserAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    MigratorService migratorService;
//    @Scheduled(fixedRate = 3600000L, initialDelay = 0)
    @Bean
    public void broadCastToStudents() {
        System.out.println("public void broadCastToStudents() {");
        List<Writersbroadcast> writersbroadcastList = writersbroadcastRepository.findToSendForPupils();
        for (Writersbroadcast writersbroadcast: writersbroadcastList) {
            List<String> pupilEmailList = getPupilEmailList();
            writersbroadcast.setTargetCount(pupilEmailList.size());
            writersbroadcast.setDateSent(new Date());
            writersbroadcastRepository.save(writersbroadcast);
            int successSent = 0;
            Boolean success;
            for (String email: pupilEmailList){
                success = mailService.sendEmail(email, writersbroadcast.getSubject(), writersbroadcast.getHtml());
                if (success)
                    successSent++;
            }
            writersbroadcast.setCountSentTo(successSent);
            writersbroadcast.setDateFinishedSending(new Date());
            writersbroadcastRepository.save(writersbroadcast);
        }
    }

    private List<String> getPupilEmailList() {
        List<String> emails = userAccountRepository.getEmails();
        emails.addAll(migratorService.queryStringArray("select distinct email from statistics where email like '%@%.%';"));
        return emails;
    }

//    @Scheduled(fixedRate = 3600000000L, initialDelay = 0)
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
                success = mailService.sendEmail(email, writersbroadcast.getSubject(), writersbroadcast.getHtml());
                if (success)
                    successSent++;
            }
            writersbroadcast.setCountSentTo(successSent);
            writersbroadcast.setDateFinishedSending(new Date());
            writersbroadcastRepository.save(writersbroadcast);
        }
    }

    private List<String> getWriterEmailList() {
        return writerUsersRepository.getEmails();
    }
}
