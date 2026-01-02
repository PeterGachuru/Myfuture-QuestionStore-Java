package ke.co.myfuture.Myfuture.UserManagement.Chatmessage;

import ke.co.myfuture.Myfuture.Utils.Response.ApiSender;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("chatmessage")
public class ChatmessageController {
    @Autowired
    ChatmessageRepository repository;

    @Autowired
    ChatmessageService chatmessageService;

    @PostMapping("add/")
    public ResponseEntity<?> newChatmessage(@RequestBody Chatmessage chatmessage) {
        try {
            chatmessage.setId(null);
            Chatmessage savedChatmessage = repository.save(chatmessage);
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Success");
            response.setMessage("Saved successfully");
            response.setEntity(savedChatmessage);
            response.setStatusCode(201);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @PostMapping("add/multiple")
    public ResponseEntity<?> newChatmessages(@RequestBody ApiSender<List<Chatmessage>> chatmessagesHolder) {
        try {
            System.out.println("add/multiple");
            List<Chatmessage> chatmessageList = new ArrayList<>();
            for (Chatmessage chatmessage: chatmessagesHolder.getEntity()) {
                System.out.println("chatmessage");
                chatmessage.setId(null);
                chatmessage.installId = chatmessagesHolder.getInstallId();
                Chatmessage savedChatmessage = repository.save(chatmessage);
                chatmessageList.add(savedChatmessage);
            }
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Success");
            response.setMessage("Saved successfully");
            response.setEntity(chatmessageList);
            response.setStatusCode(201);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PutMapping("update/")
    public ResponseEntity<?> updateChatmessage(@RequestBody Chatmessage chatmessage) {
        Chatmessage updatedChatmessage = repository.save(chatmessage);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedChatmessage);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id/{chatmessageId}")
    public ResponseEntity<?> fetchChatmessage(@PathVariable("chatmessageId") Long chatmessageId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Chatmessage retrieved Successfully");
        response.setEntity(repository.findById(chatmessageId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("get/messages")
    public ResponseEntity<?> fetchChatmessages(@RequestBody List<ChatMessageRequest> chatMessageRequests) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Chatmessages retrieved Successfully");
        response.setEntity(chatmessageService.getMessagesForGroups(chatMessageRequests));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getall/after/id/{chatmessageId}")
    public ResponseEntity<?> fetchAllAfterChatmessage(@PathVariable("chatmessageId") Long chatmessageId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Chatmessage retrieved Successfully");
        response.setEntity(repository.chatmessagesAfter(chatmessageId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

