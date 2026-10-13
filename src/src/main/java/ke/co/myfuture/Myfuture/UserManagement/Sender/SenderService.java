package ke.co.myfuture.Myfuture.UserManagement.Sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SenderService {
    @Autowired
    SenderRepository senderRepository;

    public Sender createSender(String name, SenderType senderType, Long sourceId) {
        Sender sender = new Sender();
        sender.setSourceId(sourceId);
        sender.setName(name);
        sender.setType(senderType);

        return senderRepository.save(sender);
    }
}
