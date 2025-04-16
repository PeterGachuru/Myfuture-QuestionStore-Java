package ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion;

import ke.co.myfuture.Myfuture.QuestionStore.Book.BookInitialModels;
import ke.co.myfuture.Myfuture.QuestionStore.Cgroup.Cgroup;
import ke.co.myfuture.Myfuture.QuestionStore.Cgroup.CgroupService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice.CurriNormalChoice;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice.CurriNormalChoiceRepository;
import ke.co.myfuture.Myfuture.QuestionStore.QuestionSettings.QuestionSettings;
import ke.co.myfuture.Myfuture.QuestionStore.QuestionSettings.QuestionSettingsRepo;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contest;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contestquestion.ContestQuestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CurriQuestionService {
    @Autowired
    CurriQuestionRepository curriQuestionRepository;
    @Autowired
    QuestionSettingsRepo questionSettingsRepo;

    @Autowired
    CgroupService cgroupService;

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    CurriNormalChoiceRepository curriNormalChoiceRepository;

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
}