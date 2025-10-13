package ke.co.myfuture.Myfuture.QuestionStore.CurriNotes;

import ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriNotes.ChatGPTNotesService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.Optional;


@RestController
@CrossOrigin
@RequestMapping("notes")
public class CurriNotesController {
    @Autowired
    CurriNotesRepository curriNotesRepository;

    @Autowired
    CurriTopicRepository curriTopicRepository;

    @Autowired
    ChatGPTNotesService chatGPTNotesService;
    @Autowired
    Environment env;

    @PostMapping("add")
    public ResponseEntity<?> newCurriNotes(@RequestBody CurriNotes curriNotes) {
        if (curriNotes.id != null)
            return null;

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setStatusCode(201);
        CurriNotes savedCurriNotes = curriNotesRepository.save(curriNotes);
        response.setEntity(savedCurriNotes);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateCurriNotes(@RequestBody CurriNotes notesFromUser) {
        if (notesFromUser.id == null)
            return null;
        Optional<CurriNotes> curriNotesOptional = curriNotesRepository.findById(notesFromUser.getId());

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setStatusCode(201);
        if (curriNotesOptional.isPresent()) {
            CurriNotes curriNotesDb = curriNotesOptional.get();
            curriNotesDb.update(notesFromUser);
            CurriNotes savedCurriNotes = curriNotesRepository.save(curriNotesDb);
            response.setEntity(savedCurriNotes);
        }else {
            response.setStatus("Error");
            response.setMessage("Could not save");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchCurriNotesById(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriNotes retrieved Successfully");
        Optional<CurriNotes> curriNotes = curriNotesRepository.findById(id);
        response.setEntity(curriNotes.get());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/subtopicid")
    public ResponseEntity<?> fetchCurriNotesBySubtopicId(@RequestParam("subtopicid") Long subtopicid) {
        System.out.println("........................................................");
        // 🔍 Log properties that might affect encoding
        System.out.println("server.tomcat.uri-encoding = " + env.getProperty("server.tomcat.uri-encoding"));
        System.out.println("spring.http.encoding.charset = " + env.getProperty("spring.http.encoding.charset"));
        System.out.println("spring.http.encoding.enabled = " + env.getProperty("spring.http.encoding.enabled"));
        System.out.println("spring.http.encoding.force = " + env.getProperty("spring.http.encoding.force"));
        System.out.println("spring.web.locale = " + env.getProperty("spring.web.locale"));
        System.out.println("defaultCharset = " + Charset.defaultCharset());

        // 🔍 Log Tomcat connector config
//        System.out.println("Tomcat connectors:");
//        TomcatServletWebServerFactory factory =
//                (TomcatServletWebServerFactory) context.getBean(TomcatServletWebServerFactory.class);
//
//        factory.getTomcatConnectorCustomizers().forEach(customizer -> {
//            Connector connector = new Connector();
//            customizer.customize(connector);
//
//            System.out.println("Connector URI Encoding: " + connector.getURIEncoding());
//            System.out.println("Use Body Encoding For URI: " + connector.getUseBodyEncodingForURI());
//            System.out.println("Scheme: " + connector.getScheme());
//        });

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriNotes retrieved Successfully");
        Optional<CurriTopic> curriTopic = curriTopicRepository.findById(subtopicid);
        Optional<CurriNotes> curriNotes = curriNotesRepository.findBySubtopic(curriTopic.get());
        if (curriNotes.isEmpty()) {
            chatGPTNotesService.generateNotesForSubtopic("gpt-3.5-turbo-0125", curriTopic.get());
            curriNotes = curriNotesRepository.findBySubtopic(curriTopic.get());
        } else {
            System.out.println(curriNotes.get());
        }

        response.setEntity(curriNotes.get());

        response.setStatusCode(200);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(response);
    }

    @GetMapping("all")
    public ResponseEntity<?> fetchCurriNotes() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriNotes retrieved Successfully");
        response.setEntity(curriNotesRepository.findAll());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
