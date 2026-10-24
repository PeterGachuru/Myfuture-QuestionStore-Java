package ke.co.myfuture.Myfuture.UserManagement.WebQuiz;

import ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice.CurriNormalChoice;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestion;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestionRepository;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevelRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.StudentScoreEventService;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDone;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDoneRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizQuestion.QuizQuestion;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizQuestion.QuizQuestionRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contest;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInvitee;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInviteeRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contestquestion.ContestQuestion;
import ke.co.myfuture.Myfuture.UserManagement.Contest.Contestquestion.ContestQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
//@Transactional
public class WebQuizService {

    @Autowired
    private QuizDoneRepository quizDoneRepository;

    @Autowired
    private ke.co.myfuture.Myfuture.UserManagement.ReportsAndMarketing.StudentLifecycle.StudentLifecycleEmailService studentLifecycleEmailService;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private CurriQuestionRepository curriQuestionRepository;

    @Autowired
    private SubjectLevelRepository subjectLevelRepository;

    @Autowired
    private IbukaStudentAccountRepository studentRepository;

    @Autowired
    private StudentScoreEventService studentScoreEventService;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private ContestQuestionRepository contestQuestionRepository;

    @Autowired
    private ContestInviteeRepository contestInviteeRepository;



    public QuizDone createQuiz(
            Long studentId,
            Long classLevelId,
            Long subjectId,
            int numberOfQuestions
    ) {

        /*
         * First verify that this is a valid
         * Subject + Class combination.
         */
        subjectLevelRepository
                .findValidSubjectLevel(
                        subjectId,
                        classLevelId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Subject is not available for this class"
                        ));


        IbukaStudentAccount student =
                studentRepository.findById(studentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                ));


        System.out.println("ClassLevel");
        List<CurriQuestion> available =
                curriQuestionRepository
                        .findQuizQuestions(
                                classLevelId,
                                subjectId
                        );


        if (available.isEmpty()) {

            throw new RuntimeException(
                    "No questions are currently available"
            );
        }


        /*
         * Randomize questions.
         */
        Collections.shuffle(available);


        int count =
                Math.min(
                        numberOfQuestions,
                        available.size()
                );


        List<CurriQuestion> selected =
                new ArrayList<>(
                        available.subList(0, count)
                );


        /*
         * Create QuizDone.
         */
        QuizDone quiz =
                new QuizDone();

        quiz.questionsCount =
                selected.size();

        quiz.student =
                student;

        quiz.startDate =
                new Date();

        quiz.subjectId =
                subjectId;

        quiz.score = 0;

        quiz.overall =
                selected.size();

        quiz.deleted =
                false;

        quiz.platform =
                "WEB";

        /*
         * We should revisit these two fields
         * as discussed below.
         */
        quiz.inid =
                System.currentTimeMillis();

        quiz.installId =
                0L;


        QuizDone saved =
                quizDoneRepository.save(quiz);


        /*
         * Create QuizQuestion records.
         */
        List<QuizQuestion> quizQuestions =
                new ArrayList<>();


        int position = 1;


        for (CurriQuestion question : selected) {

            QuizQuestion qq =
                    new QuizQuestion();

            qq.quiz =
                    saved.id;

            qq.questionId =
                    question.id;

            qq.position =
                    position++;


            /*
             * Randomize choice order.
             */
            List<Long> choices =
                    question.getChoices()
                            .stream()
                            .map(c -> c.id)
                            .collect(Collectors.toList());

            Collections.shuffle(choices);


            qq.choicesOrder =
                    choices.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));


            qq.selectedChoice =
                    null;

            qq.gotCorrect =
                    null;


            quizQuestions.add(qq);
        }


        quizQuestionRepository
                .saveAll(quizQuestions);


        return saved;
    }

    public QuizDone createContestQuiz(Long studentId, Long contestId) {
        IbukaStudentAccount student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));

        ContestInvitee invitee = contestInviteeRepository
                .findByContestAndStudentaccount(contestId, student)
                .orElseThrow(() -> new RuntimeException("Student is not invited to this contest"));

        if (invitee.isAttempted()) {
            return quizDoneRepository
                    .findFirstByContest_IdAndStudent_IdOrderByCreatedAtDesc(contestId, studentId)
                    .orElseThrow(() -> new RuntimeException("Contest already attempted"));
        }

        // Resume an unfinished web attempt if one exists.
        Optional<QuizDone> existing = quizDoneRepository
                .findFirstByContest_IdAndStudent_IdOrderByCreatedAtDesc(contestId, studentId);
        if (existing.isPresent() && existing.get().getEndDate() == null) {
            return existing.get();
        }

        List<ContestQuestion> contestQuestions =
                contestQuestionRepository.findByContestOrderByIdAsc(contestId);

        if (contestQuestions.isEmpty()) {
            throw new RuntimeException("This contest has no questions");
        }

        QuizDone quiz = new QuizDone();
        quiz.questionsCount = contestQuestions.size();
        quiz.student = student;
        quiz.contest = contest;
        quiz.startDate = new Date();
        quiz.subjectId = contest.subjectId;
        quiz.score = 0;
        quiz.overall = contestQuestions.size();
        quiz.deleted = false;
        quiz.platform = "WEB";
        quiz.category = "CONTEST";
        quiz.inid = System.currentTimeMillis();
        quiz.installId = 0L;

        QuizDone saved = quizDoneRepository.save(quiz);

        List<QuizQuestion> quizQuestions = new ArrayList<>();
        int position = 1;

        for (ContestQuestion contestQuestion : contestQuestions) {
            CurriQuestion question = curriQuestionRepository.findById(contestQuestion.getQuestion())
                    .orElseThrow(() -> new RuntimeException("Contest question not found"));

            QuizQuestion qq = new QuizQuestion();
            qq.quiz = saved.id;
            qq.questionId = question.id;
            qq.position = position++;

            List<Long> choices = question.getChoices().stream()
                    .map(c -> c.id)
                    .collect(Collectors.toList());
            Collections.shuffle(choices);
            qq.choicesOrder = choices.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            quizQuestions.add(qq);
        }

        quizQuestionRepository.saveAll(quizQuestions);
        return saved;
    }

    public WebQuizQuestionDTO getQuestion(
            Long quizId,
            int position
    ) {

        QuizDone quiz = quizDoneRepository.findById(quizId)
                .orElseThrow(() ->
                        new RuntimeException("Quiz not found"));


        List<QuizQuestion> quizQuestions =
                quizQuestionRepository
                        .findByQuizOrderByIdAsc(quizId);


        if (position < 1 ||
                position > quizQuestions.size()) {

            throw new RuntimeException(
                    "Invalid question"
            );
        }


        QuizQuestion qq =
                quizQuestions.get(position - 1);


        CurriQuestion question =
                curriQuestionRepository
                        .findById(qq.questionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Question not found"
                                ));


        /*
         * Put the question choices into a map so that
         * we can reconstruct the order stored in
         * QuizQuestion.choicesOrder.
         */
        Map<Long, CurriNormalChoice> choices =
                question.getChoices()
                        .stream()
                        .collect(Collectors.toMap(
                                c -> c.id,
                                c -> c
                        ));


        /*
         * Reconstruct the choices in the exact order
         * in which they were presented to the student.
         */
        List<WebQuizChoiceDTO> choiceDTOs =
                Arrays.stream(
                                qq.choicesOrder.split(",")
                        )
                        .map(Long::valueOf)
                        .map(choices::get)
                        .filter(Objects::nonNull)
                        .map(c ->
                                new WebQuizChoiceDTO(
                                        c.id,
                                        c.getValue(),
                                        c.isCorrect()
                                )
                        )
                        .collect(Collectors.toList());


        /*
         * Build the question DTO.
         */
        return new WebQuizQuestionDTO(
                qq.id,
                question.id,
                position,
                question.getString(),
                choiceDTOs,
                qq.selectedChoice,
                qq.selectedChoice != null,
                qq.gotCorrect,
                question.getExplanation()
        );
    }
//    @Transactional
    public void answerQuestion(
            Long quizId,
            Long questionId,
            Long selectedChoice
    ) {

        QuizQuestion quizQuestion =
                quizQuestionRepository
                        .findByQuizAndQuestionId(
                                quizId,
                                questionId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid quiz question"
                                ));


        CurriQuestion question =
                curriQuestionRepository
                        .findById(questionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Question not found"
                                ));


        CurriNormalChoice choice =
                question.getChoices()
                        .stream()
                        .filter(c ->
                                c.id.equals(selectedChoice))
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid choice"
                                ));


        quizQuestion.selectedChoice =
                selectedChoice;


        quizQuestion.gotCorrect =
                choice.isCorrect();


        quizQuestionRepository.save(
                quizQuestion
        );
    }

    @Transactional(transactionManager = "studentManagementTransactionManager")
    public QuizDone finishQuiz(Long quizId) {

        QuizDone quiz =
                quizDoneRepository
                        .findById(quizId)
                        .orElseThrow();

        // Prevent repeated result-page refreshes from awarding points repeatedly.
        if (quiz.endDate != null) {
            studentLifecycleEmailService.onQuizCompleted(quiz);
            return quiz;
        }

        List<QuizQuestion> questions =
                quizQuestionRepository
                        .findByQuizOrderByPositionAsc(
                                quizId
                        );


        int score =
                (int) questions.stream()
                        .filter(q ->
                                Boolean.TRUE.equals(
                                        q.gotCorrect
                                ))
                        .count();


        quiz.score =
                score;

        quiz.overall =
                questions.size();

        quiz.endDate =
                new Date();


        QuizDone savedQuiz = quizDoneRepository.save(quiz);
        IbukaStudentAccount ibukaStudentAccount = savedQuiz.getStudent();
        long scoreAward = (long) score * 2L;
        if (scoreAward > 0 && savedQuiz.getId() != null) {
            studentScoreEventService.recordTrusted(
                    "web-quiz:" + savedQuiz.getId(),
                    ibukaStudentAccount.getId(),
                    scoreAward,
                    "web_quiz",
                    savedQuiz.getInstallId(),
                    savedQuiz.getEndDate() == null ? null : savedQuiz.getEndDate().toInstant()
            );
        }

        if (savedQuiz.getContest() != null) {
            contestInviteeRepository.updateScore(
                    savedQuiz.getContest().getId(),
                    ibukaStudentAccount.getId(),
                    score
            );
        }

        studentLifecycleEmailService.onQuizCompleted(savedQuiz);
        return savedQuiz;
    }

    public QuizDone getStudentQuiz(
            Long quizId,
            Long studentId
    ) {

        if (quizId == null || studentId == null) {
            return null;
        }

        return quizDoneRepository
                .findByIdAndStudent_Id(
                        quizId,
                        studentId
                )
                .orElse(null);
    }

    public List<WebQuizQuestionDTO> getResults(
            Long quizId
    ) {

        List<QuizQuestion> quizQuestions =
                quizQuestionRepository
                        .findByQuizOrderByIdAsc(quizId);


        List<WebQuizQuestionDTO> results =
                new ArrayList<>();


        for (QuizQuestion quizQuestion : quizQuestions) {

            Optional<CurriQuestion> questionOptional =
                    curriQuestionRepository.findById(
                            quizQuestion.questionId
                    );


            if (questionOptional.isEmpty()) {
                continue;
            }


            CurriQuestion question =
                    questionOptional.get();


            List<WebQuizChoiceDTO> choices =
                    new ArrayList<>();


            if (question.getChoices() != null) {

                for (CurriNormalChoice choice :
                        question.getChoices()) {

                    choices.add(
                            new WebQuizChoiceDTO(
                                    choice.id,
                                    choice.getValue(),
                                    choice.isCorrect()
                            )
                    );
                }
            }


            WebQuizQuestionDTO dto =
                    new WebQuizQuestionDTO();

            dto.setQuestionId(
                    question.id
            );

            dto.setQuestion(
                    question.getString()
            );

            dto.setChoices(
                    choices
            );

            dto.setSelectedChoice(
                    quizQuestion.selectedChoice
            );

            dto.setGotCorrect(
                    quizQuestion.gotCorrect
            );

            dto.setExplanation(
                    question.getExplanation()
            );


            results.add(dto);
        }


        return results;
    }

    public List<QuizQuestion> getQuizQuestions(Long quizId) {

        if (quizId == null) {
            return Collections.emptyList();
        }

        return quizQuestionRepository
                .findByQuizOrderByIdAsc(quizId);
    }
}
