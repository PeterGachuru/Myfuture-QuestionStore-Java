package ke.co.myfuture.Myfuture.UserManagement.Chatmessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ChatmessageService {
    @Autowired
    ChatmessageRepository chatmessageRepository;

    public List<Chatmessage> getMessagesForGroups(List<ChatMessageRequest> chatMessageRequests) {
        List<Chatmessage> chatmessageList = new ArrayList<>();

        for (ChatMessageRequest chatMessageRequest: chatMessageRequests){
            chatmessageList.addAll(chatmessageRepository.getMessagesForGroup(chatMessageRequest.groupId, chatMessageRequest.latestmessageId));
        }
        return chatmessageList;
    }
}

