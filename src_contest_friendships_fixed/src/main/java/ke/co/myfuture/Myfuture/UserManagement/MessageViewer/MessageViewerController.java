package ke.co.myfuture.Myfuture.UserManagement.MessageViewer;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("messageviewer")
public class MessageViewerController {
    @Autowired
    MessageViewerRepository repository;



    @PostMapping("add/")
    public ResponseEntity<?> newChatmessage(@RequestBody MessageViewer messageViewer) {
        try {
            messageViewer.setId(null);
            MessageViewer savedChatmessage = repository.save(messageViewer);
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
    public ResponseEntity<?> newChatmessages(@RequestBody List<MessageViewer> messageViewers) {
        try {
            List<MessageViewer> messageViewerList = new ArrayList<>();
            for (MessageViewer messageViewer: messageViewers){
                messageViewer.setId(null);
                MessageViewer savedChatmessage = repository.save(messageViewer);
                messageViewerList.add(savedChatmessage);
            }
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Success");
            response.setMessage("Saved successfully");
            response.setEntity(messageViewerList);
            response.setStatusCode(201);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PutMapping("update/")
    public ResponseEntity<?> updateMessageViewer(@RequestBody MessageViewer messageviewer) {
        MessageViewer updatedMessageViewer = repository.save(messageviewer);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedMessageViewer);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id/{messageviewerId}")
    public ResponseEntity<?> fetchMessageViewer(@PathVariable("messageviewerId") Long messageviewerId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("MessageViewer retrieved Successfully");
        response.setEntity(repository.findById(messageviewerId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getall/after/id/{messageviewerId}")
    public ResponseEntity<?> fetchAllAfterMessageViewer(@PathVariable("messageviewerId") Long messageviewerId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("MessageViewer retrieved Successfully");
        response.setEntity(repository.messageviewersAfter(messageviewerId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
