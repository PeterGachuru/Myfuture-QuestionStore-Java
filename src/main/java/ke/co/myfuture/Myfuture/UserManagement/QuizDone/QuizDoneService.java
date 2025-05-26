package ke.co.myfuture.Myfuture.UserManagement.QuizDone;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contest;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizQuestion.QuizQuestion;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizQuestion.QuizQuestionRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizDoneService {
    @Autowired
    IbukaStudentAccountRepository ibukaStudentAccountRepository;
    @Autowired
    ContestRepository contestRepository;
    @Autowired
    QuizDoneRepository quizDoneRepository;
    @Autowired
    QuizQuestionRepository quizQuestionRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    CurriLevelRepository curriLevelRepository;

    public List<QuizDoneDTO> findAll() {
        // Load and cache all subjects
        Map<Long, Subject> subjectMap = subjectRepository.findAll()
                .stream()
                .collect(Collectors.toMap(subject -> subject.getId(), subject -> subject));

        // Load and cache all curriLevels
        Map<Long, CurriLevel> curriLevelMap = curriLevelRepository.findAll()
                .stream()
                .collect(Collectors.toMap(curriLevel -> curriLevel.getId(), curriLevel -> curriLevel));

        // Load all quizzes
        List<QuizDone> quizDones =  quizDoneRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        // Convert to DTOs
        List<QuizDoneDTO> dtos = quizDones.stream()
                .map(quiz -> {
                    Subject subject = subjectMap.getOrDefault(quiz.subjectId, null);
                    CurriLevel curriLevel = (quiz.student != null) ? curriLevelMap.getOrDefault(quiz.student.getClasslevel(), null) : null;
                    return new QuizDoneDTO(quiz, subject, curriLevel);
                })
                .collect(Collectors.toList());

        return dtos;
    }

    public Optional<QuizDone> createQuiz(CreateQuizDone createQuizDone) {
        Optional<IbukaStudentAccount> creator = ibukaStudentAccountRepository.findById(createQuizDone.studentId);
        if (creator.isEmpty()) return Optional.empty();

        QuizDone quizDone = new QuizDone();
        quizDone.questionsCount = createQuizDone.questionsCount;
        quizDone.student = creator.get();
        quizDone.startDate = createQuizDone.startDate;
        quizDone.endDate = createQuizDone.endDate;
        quizDone.score = createQuizDone.score;
        quizDone.inid = createQuizDone.inid;
        quizDone.appVersion = createQuizDone.appVersion;
        quizDone.installId = createQuizDone.installId;
        quizDone.category = createQuizDone.category;
        quizDone.overall = createQuizDone.overall;
        quizDone.subjectId = createQuizDone.subjectId;
        quizDone.deleted = createQuizDone.deleted;

        if (createQuizDone.contestId != null) {
            Optional<Contest> contest = contestRepository.findById(createQuizDone.contestId );
            contest.ifPresent(value -> quizDone.contest = value);
        }

        QuizDone createdQuiz = quizDoneRepository.save(quizDone);

        List<QuizQuestion> quizQuestionList = new ArrayList<>();

        for (CreateQuizDone.CreateQuizQuestion createQuizQuestion: createQuizDone.questions) {
            QuizQuestion quizQuestion = new QuizQuestion();
            quizQuestion.gotCorrect = createQuizQuestion.gotCorrect;
            quizQuestion.selectedChoice = createQuizQuestion.selectedChoice;
            quizQuestion.questionId = createQuizQuestion.questionId;
            quizQuestion.choicesOrder = createQuizQuestion.choicesOrder;
            quizQuestion.quiz = createdQuiz.id;

            quizQuestionList.add(quizQuestion);
        }

        quizQuestionRepository.saveAll(quizQuestionList);
        return quizDoneRepository.findById(createdQuiz.id);
    }
}
