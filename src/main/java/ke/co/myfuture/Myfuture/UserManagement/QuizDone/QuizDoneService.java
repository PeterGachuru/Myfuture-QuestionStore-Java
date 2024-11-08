package ke.co.myfuture.Myfuture.UserManagement.QuizDone;

import ke.co.myfuture.Myfuture.UserManagement.Contest.Contest;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizQuestion.QuizQuestion;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizQuestion.QuizQuestionRepository;
import ke.co.myfuture.Myfuture.UserManagement.Studentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.Studentaccount.StudentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizDoneService {
    @Autowired
    StudentAccountRepository studentAccountRepository;
    @Autowired
    ContestRepository contestRepository;
    @Autowired
    QuizDoneRepository quizDoneRepository;
    @Autowired
    QuizQuestionRepository quizQuestionRepository;
    public Optional<QuizDone> createQuiz(CreateQuizDone createQuizDone) {
        Optional<IbukaStudentAccount> creator = studentAccountRepository.findById(createQuizDone.studentId);
        if (creator.isEmpty()) return Optional.empty();

        QuizDone quizDone = new QuizDone();
        quizDone.questionsCount = createQuizDone.questionsCount;
        quizDone.student = creator.get();
        quizDone.startDate = createQuizDone.startDate;
        quizDone.endDate = createQuizDone.endDate;
        quizDone.score = createQuizDone.score;
        quizDone.inid = createQuizDone.inid;
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
