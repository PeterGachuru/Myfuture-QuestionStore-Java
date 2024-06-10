package ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion;

import ke.co.myfuture.Myfuture.ImageStore.FileManagement.ImageFile;
import ke.co.myfuture.Myfuture.ImageStore.FileManagement.ImageFileService;
import ke.co.myfuture.Myfuture.QuestionStore.Cgroup.Cgroup;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice.CurriNormalChoice;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice.CurriNormalChoiceRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ke.co.myfuture.Myfuture.QuestionStore.Cgroup.CgroupService;

import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@CrossOrigin
@RequestMapping("questionstore/questions")
public class CurriQuestionController {
    @Autowired
    CurriQuestionRepository repository;

    @Autowired
    ImageFileService imageFileService;

    @Autowired
    CurriTopicRepository curriTopicRepository;

    @Autowired
    CgroupService cgroupService;

    @Autowired
    CurriNormalChoiceRepository curriNormalChoiceRepository;

    @PostMapping("add/{subtopic}")
    public ResponseEntity<?> newCurriQuestion(@PathVariable("") Long subtopic, @RequestBody CurriQuestion question) {
        Optional<CurriTopic> curriSubtopic = curriTopicRepository.findById(subtopic);
        if (curriSubtopic.isPresent()) {
            question.setSubtopic(curriSubtopic.get());

            Cgroup cgroup = new Cgroup();
            cgroup.setType("Many");
            cgroup.setDescription("Question group");
            cgroup.setName("Question group");

            cgroup = cgroupService.newCgroup(cgroup);

            question.setCgroup(cgroup.id);

            List<CurriNormalChoice> choices = question.getChoices();
//            question.updateChoices();

            CurriQuestion savedCurriQuestion = repository.save(question);
//
            for (CurriNormalChoice choice: choices) {
                System.out.println(choice);
                choice.setQuestion(savedCurriQuestion.getId());
            }
            curriNormalChoiceRepository.saveAll(choices);
            savedCurriQuestion = repository.findById(savedCurriQuestion.getId()).get();

            System.out.println(savedCurriQuestion);
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Success");
            response.setMessage("Saved successfully");
            response.setEntity(savedCurriQuestion);
            response.setStatusCode(201);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return null;
    }

    @PutMapping("update")
    public ResponseEntity<?> updateCurriQuestion(@RequestBody CurriQuestion question) {
        Optional<CurriQuestion> dbCurriQuestion = repository.findById(question.id);
        if (dbCurriQuestion.isPresent()) {
            CurriQuestion curriQuestion = dbCurriQuestion.get();
            curriQuestion.setString(question.getString());
            curriQuestion.setHasImage(question.getHasImage());
            curriQuestion.setImageCode(question.getImageCode());
//            curriQuestion.setImageLevel(question.getImageLevel());

            Map<Long, CurriNormalChoice> mapForIncomingChoices = new HashMap<>();
            for (CurriNormalChoice curriNormalChoice : question.choices) {
                mapForIncomingChoices.put(curriNormalChoice.getId(), curriNormalChoice);
            }

            List<CurriNormalChoice> newCurriNormalChoices = new ArrayList<>();
            for (CurriNormalChoice curriNormalChoice: dbCurriQuestion.get().getChoices()) {
                CurriNormalChoice incomingChoice = mapForIncomingChoices.get(curriNormalChoice.getId());
                if (incomingChoice != null) {
                    curriNormalChoice.setImageCode(incomingChoice.getImageCode());
                    curriNormalChoice.setValue(incomingChoice.getValue());
                    curriNormalChoice.setType(incomingChoice.getType());
                    newCurriNormalChoices.add(curriNormalChoice);
                }
            }
            curriQuestion.setChoices(question.getChoices());
            CurriQuestion savedSubquestion = repository.save(curriQuestion);
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Success");
            response.setMessage("Updated Successfully");
            response.setEntity(savedSubquestion);
            response.setStatusCode(201);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Could not update");
        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchCurriQuestion(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriQuestion retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/all/curriculum")
    public ResponseEntity<?> fetchCurriQuestion(@RequestParam("model") String model,
                                                @RequestParam("lastUpdateId") String lastUpdateId,
                                                @RequestParam("curriculum") Long curriculum,
                                                @RequestParam("page") int page,
                                                @RequestParam("size") int size) {
        System.out.println("model: "+model+", lastUpdateId: "+lastUpdateId+", curriculum: "+curriculum+", page: "+", "+size);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriQuestion retrieved Successfully");
        System.out.println(new Date());
        System.out.println("Started reading questions");
        Pageable paging = PageRequest.of(page, size);
        Page<CurriQuestion> curriQuestions = repository.findByBookModel(paging, model, lastUpdateId, curriculum);
        System.out.println(curriQuestions.getContent().size());
        response.setEntity(curriQuestions.getContent());
        response.setCurrentPage(page);
        response.setTotalItems(curriQuestions.getSize());
        response.setTotalPages(curriQuestions.getTotalPages());
        System.out.println("Completed reading questions");
        System.out.println(new Date());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/all/level/and/subject")
    public ResponseEntity<?> fetchCurriQuestion(@RequestParam("model") String model,
                                                @RequestParam("lastUpdateId") String lastUpdateId,
                                                @RequestParam("curriculum") Long curriculum,
                                                @RequestParam("level") Long level,
                                                @RequestParam("subject") Long subject,
                                                @RequestParam("page") int page,
                                                @RequestParam("size") int size) {
        System.out.println("model: "+model+", lastUpdateId: "+lastUpdateId+", curriculum: "+curriculum+", level: "+level+", subject: "+subject+", page: "+", "+size);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriQuestion retrieved Successfully");
        System.out.println(new Date());
        System.out.println("Started reading questions");
        Pageable paging = PageRequest.of(page, size);
        Page<CurriQuestion> curriQuestions = repository.findByBookModel(paging, model, lastUpdateId, curriculum, level, subject);
        System.out.println(curriQuestions.getContent().size());
        response.setEntity(curriQuestions.getContent());
        response.setCurrentPage(page);
        response.setTotalItems(curriQuestions.getSize());
        response.setTotalPages(curriQuestions.getTotalPages());
        System.out.println("Completed reading questions");
        System.out.println(new Date());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/subtopic")
    public ResponseEntity<?> fetchCurriQuestionByParent(@RequestParam("subtopicId") Long subtopicId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriQuestion retrieved Successfully");
        response.setEntity(repository.findBySubtopicId(subtopicId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(path = "attach-image/{id}", method = POST,  consumes = { MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> newFile(@RequestParam("image") MultipartFile fileUploaded, @PathVariable Long id) {
        ImageFile imageFile = imageFileService.save(fileUploaded);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        Optional<CurriQuestion> curriNormalChoice = repository.findById(id);
        if (imageFile != null && curriNormalChoice.isPresent()) {
            curriNormalChoice.get().setHasImage(true);
            curriNormalChoice.get().setImageCode(imageFile.getCode());
            repository.save(curriNormalChoice.get());
            response.setStatus("Success");
            response.setMessage("Saved successfully");
            response.setEntity(curriNormalChoice.get());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
