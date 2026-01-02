package ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion;

import ke.co.myfuture.Myfuture.ImageStore.FileManagement.ImageFile;
import ke.co.myfuture.Myfuture.ImageStore.FileManagement.ImageFileService;
import ke.co.myfuture.Myfuture.QuestionStore.Book.BookInitialModels;
import ke.co.myfuture.Myfuture.UserManagement.Cgroup.Cgroup;
import ke.co.myfuture.Myfuture.UserManagement.Cgroup.CgroupService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice.CurriNormalChoice;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice.CurriNormalChoiceRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.QuestionStore.QuestionSettings.QuestionSettings;
import ke.co.myfuture.Myfuture.QuestionStore.QuestionSettings.QuestionSettingsRepo;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contest;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contestquestion.ContestQuestion;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class CurriQuestionService {
    @Autowired
    CurriQuestionRepository curriQuestionRepository;
    @Autowired
    ImageFileService imageFileService;
    @Autowired
    QuestionSettingsRepo questionSettingsRepo;

    @Autowired
    CgroupService cgroupService;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    CurriNormalChoiceRepository curriNormalChoiceRepository;
    @Autowired
    CurriTopicRepository curriTopicRepository;
    @Bean
    private void updateBookModel() {
        curriQuestionRepository.setDefaultBookModel(BookInitialModels.written1Version);
    }


    public void saveNewQuestion(CurriQuestion curriQuestion, List<CurriNormalChoice> choices, long updateId) {
        Cgroup cgroup = new Cgroup();
        cgroup.setType("Many");
        cgroup.setDescription("Question group");
        cgroup.setName("Question group");

        cgroup = cgroupService.newCgroup(cgroup);

        curriQuestion.setCgroup(cgroup.id);
        curriQuestion.setUpdateId(updateId);
        CurriQuestion savedCurriQuestion = curriQuestionRepository.save(curriQuestion);
//
        for (CurriNormalChoice choice: choices) {
            choice.setQuestion(savedCurriQuestion.getId());
        }
        curriNormalChoiceRepository.saveAll(choices);
    }

    public long getNewUpdateId() {
        Optional<QuestionSettings> questionSettingsOptional = questionSettingsRepo.findByActiveAndCode(true, QuestionSettings.latestQuestionUpdateId);
        if (questionSettingsOptional.isEmpty()) {
            QuestionSettings questionSettings = new QuestionSettings();
            questionSettings.setCode(QuestionSettings.latestQuestionUpdateId);
            questionSettings.setSettingValue(String.valueOf(1));
            questionSettings.setDataType("long");
            questionSettings.setDescription("Increments on every question insert/update");
            questionSettings.setActive(true);
            questionSettings = questionSettingsRepo.save(questionSettings);
            return Long.parseLong(questionSettings.getSettingValue());
        } else {
            long value = Long.parseLong(questionSettingsOptional.get().getSettingValue());
            questionSettingsOptional.get().setSettingValue(String.valueOf(value+1));
            System.out.println("New setting value: "+value);
            return Long.parseLong(questionSettingsRepo.save(questionSettingsOptional.get()).getSettingValue());
        }
    }

    public List<CurriQuestion> forContestQuestionDownload(Long contestId) {
        Optional<Contest> contestOptional = contestRepository.findById(contestId);
        ArrayList<Long> questions = new ArrayList<>();

        for (ContestQuestion contestQuestion: contestOptional.get().contestQuestions) {
            System.out.print(", "+contestQuestion.getQuestion());
            questions.add(contestQuestion.getQuestion());
        }

        return curriQuestionRepository.forContestDownload(questions);
    }

    public ResponseEntity<?> newCurriQuestion(Long subtopic, CurriQuestion question) {
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

            CurriQuestion savedCurriQuestion = curriQuestionRepository.save(question);
//
            for (CurriNormalChoice choice: choices) {
                System.out.println(choice);
                choice.setQuestion(savedCurriQuestion.getId());
            }
            curriNormalChoiceRepository.saveAll(choices);
            savedCurriQuestion = curriQuestionRepository.findById(savedCurriQuestion.getId()).get();

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

    public ResponseEntity<?> updateCurriQuestion( CurriQuestion question) {
        Optional<CurriQuestion> dbCurriQuestion = curriQuestionRepository.findById(question.id);
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
            CurriQuestion savedSubquestion = curriQuestionRepository.save(curriQuestion);
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

    public ResponseEntity<?> approveCurriQuestion(Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Question approved Successfully");
        Optional<CurriQuestion> curriQuestion = curriQuestionRepository.findById(id);
        curriQuestion.get().approve();
        curriQuestion.get().setUpdateId(getNewUpdateId());
        curriQuestion.get().setApprovalDate(new Date());
        curriQuestionRepository.save(curriQuestion.get());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> deleteCurriQuestion( Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Question deleted");
        Optional<CurriQuestion> curriQuestion = curriQuestionRepository.findById(id);
        curriQuestion.get().delete();
        curriQuestion.get().setUpdateId(getNewUpdateId());
        curriQuestionRepository.save(curriQuestion.get());
        response.setEntity(null);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteCurriQuestion(Long subjectId,  String bookModel) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Questions deleted");
        List<CurriQuestion> curriQuestions = curriQuestionRepository.findBySubjectAndBookModel(subjectId, bookModel);

        List<CurriQuestion> updatedQuestions = new ArrayList<>();
        for (CurriQuestion curriQuestion: curriQuestions) {
            curriQuestion.delete();
            curriQuestion.setUpdateId(getNewUpdateId());
            updatedQuestions.add(curriQuestion);
        }
        curriQuestionRepository.saveAll(updatedQuestions);
        response.setEntity(null);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<?> fetchCurriQuestion( String model,
                                                 String lastUpdateId,
                                                 Long curriculum,
                                                 Long level,
                                                 Long subject,
                                                 int page,
                                                 int size) {
        System.out.println("model: "+model+", lastUpdateId: "+lastUpdateId+", curriculum: "+curriculum+", level: "+level+", subject: "+subject+", page: "+", "+size);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriQuestion retrieved Successfully");
        System.out.println(new Date());
        System.out.println("Started reading questions");
        Pageable paging = PageRequest.of(page, size);
        Page<CurriQuestion> curriQuestions = curriQuestionRepository.findByBookModel(paging, model, lastUpdateId, curriculum, level, subject);
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

    public ResponseEntity<?> newFile( MultipartFile fileUploaded, Long id) {
        ImageFile imageFile = imageFileService.save(fileUploaded);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        Optional<CurriQuestion> curriNormalChoice = curriQuestionRepository.findById(id);
        if (imageFile != null && curriNormalChoice.isPresent()) {
            curriNormalChoice.get().setHasImage(true);
            curriNormalChoice.get().setImageCode(imageFile.getCode());
            curriQuestionRepository.save(curriNormalChoice.get());
            response.setStatus("Success");
            response.setMessage("Saved successfully");
            response.setEntity(curriNormalChoice.get());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> fetchCurriQuestion(String model,
                                                String lastUpdateId,
                                                 Long curriculum,
                                                 int page,
                                                 int size) {
        System.out.println("model: "+model+", lastUpdateId: "+lastUpdateId+", curriculum: "+curriculum+", page: "+", "+size);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("CurriQuestion retrieved Successfully");
        System.out.println(new Date());
        System.out.println("Started reading questions");
        Pageable paging = PageRequest.of(page, size);
        Page<CurriQuestion> curriQuestions = curriQuestionRepository.findByBookModel(paging, model, lastUpdateId, curriculum);
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
}