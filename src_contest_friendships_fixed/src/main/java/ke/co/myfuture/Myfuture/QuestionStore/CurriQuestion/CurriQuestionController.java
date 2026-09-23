package ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion;

import ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriQuestion.ChatGPTQuestionsService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice.CurriNormalChoiceRepository;
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
import ke.co.myfuture.Myfuture.UserManagement.Cgroup.CgroupService;

import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@CrossOrigin
@RequestMapping("questionstore/questions")
public class CurriQuestionController {
    @Autowired
    CurriQuestionRepository repository;

    @Autowired
    CurriTopicRepository curriTopicRepository;

    @Autowired
    CurriQuestionRepository curriQuestionRepository;

    @Autowired
    ChatGPTQuestionsService chatGPTQuestionsService;

    @Autowired
    CgroupService cgroupService;

    @Autowired
    CurriQuestionService curriQuestionService;

    @Autowired
    CurriNormalChoiceRepository curriNormalChoiceRepository;

    @PostMapping("add/{subtopic}")
    public ResponseEntity<?> newCurriQuestion(@PathVariable("") Long subtopic, @RequestBody CurriQuestion question) {
        return curriQuestionService.newCurriQuestion(subtopic, question);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateCurriQuestion(@RequestBody CurriQuestion question) {
        return curriQuestionService.updateCurriQuestion(question);
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

    @PutMapping("approve")
    public ResponseEntity<?> approveCurriQuestion(@RequestParam("id") Long id) {
        return curriQuestionService.approveCurriQuestion(id);
    }


    @DeleteMapping("delete")
    public ResponseEntity<?> deleteCurriQuestion(@RequestParam("id") Long id) {
        return curriQuestionService.deleteCurriQuestion(id);
    }

    @DeleteMapping("deleteBySubject")
    public ResponseEntity<?> deleteCurriQuestion(@RequestParam("subjectId") Long subjectId, @RequestParam("bookModel") String bookModel) {
        return curriQuestionService.deleteCurriQuestion(subjectId, bookModel);
    }

    @GetMapping("forContestQuestionDownload")
    public ResponseEntity<?> forContestQuestionDownload(@RequestParam("contestId") Long contestId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriQuestion retrieved Successfully");
        response.setEntity(curriQuestionService.forContestQuestionDownload(contestId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/all/curriculum")
    public ResponseEntity<?> fetchCurriQuestion(@RequestParam("model") String model,
                                                @RequestParam("lastUpdateId") String lastUpdateId,
                                                @RequestParam("curriculum") Long curriculum,
                                                @RequestParam("page") int page,
                                                @RequestParam("size") int size) {

        return curriQuestionService.fetchCurriQuestion(model, lastUpdateId, curriculum, page, size);
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
        Page<CurriQuestion> curriQuestions = curriQuestionRepository.findByBookModel(paging, model, lastUpdateId, curriculum, level, subject);
        System.out.println(curriQuestions.getContent().size());

        if (Long.parseLong(lastUpdateId) < 5 && curriQuestions.getContent().size() < 5) {
            chatGPTQuestionsService.generateQuestionsForSubject(model, level, subject);
        }
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

    @GetMapping("all/withunapprovedquestions")
    public ResponseEntity<?> fetchCurriQuestionByParentUnapproved(@RequestParam("subtopicId") Long subtopicId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriQuestion retrieved Successfully");
        response.setEntity(repository.findBySubtopicIdUnapproved(subtopicId));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "attach-image/{id}", method = POST,  consumes = { MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> newFile(@RequestParam("image") MultipartFile fileUploaded, @PathVariable Long id) {
        return curriQuestionService.newFile(fileUploaded, id);
    }
}
