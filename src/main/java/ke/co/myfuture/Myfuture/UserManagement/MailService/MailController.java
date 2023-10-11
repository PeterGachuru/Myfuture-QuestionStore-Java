package ke.co.myfuture.Myfuture.UserManagement.MailService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/mail")
@Slf4j
public class MailController {
    @Autowired
    private MailService mailService;


    @PostMapping(path = "/send/email")
    public ResponseEntity<?> sendEmail(@RequestBody MailDto mailDto) {
        try {
            mailService.sendEmail(mailDto.getTo(),  mailDto.getMessage(),  mailDto.getSubject());
            return ResponseEntity.ok("mail sent");
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }

    }
}
