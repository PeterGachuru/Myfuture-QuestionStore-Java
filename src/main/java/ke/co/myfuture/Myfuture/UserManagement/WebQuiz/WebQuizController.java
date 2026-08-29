package ke.co.myfuture.Myfuture.UserManagement.WebQuiz;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.LoginSession;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevel;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevelRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDone;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizQuestion.QuizQuestion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/read/webquiz")
@Slf4j
public class WebQuizController {

    @Autowired
    private WebQuizService webQuizService;

    @Autowired
    private CurriLevelRepository curriLevelRepository;

    @Autowired
    private SubjectLevelRepository subjectLevelRepository;

    @Autowired
    private IbukaStudentAccountRepository ibukaStudentAccountRepository;


    /**
     * Start a new quiz.
     *
     * The student is obtained from the HTTP session.
     * We intentionally do NOT accept studentId from
     * the browser.
     */
    @GetMapping("/start/{classLevelId}/{subjectId}")
    public String startQuiz(
            @PathVariable Long classLevelId,
            @PathVariable Long subjectId,
            HttpServletRequest request
    ) {

        log.info("========== START QUIZ REQUEST ==========");
        log.info("Requested classLevelId: {}", classLevelId);
        log.info("Requested subjectId: {}", subjectId);

        try {

            // ---------------------------------------------------------
            // 1. Check login session
            // ---------------------------------------------------------
            LoginSession user =
                    (LoginSession) request.getSession()
                            .getAttribute("user");

            if (user == null) {
                log.warn("START QUIZ FAILED: No LoginSession found in session.");
                log.warn("Redirecting user to /read/login");

                return "redirect:/read/login";
            }

            log.info("Logged-in user found. User ID: {}", user.getUserId());


            // ---------------------------------------------------------
            // 2. Check selected student
            // ---------------------------------------------------------
            IbukaStudentAccount student =
                    (IbukaStudentAccount) request.getSession()
                            .getAttribute("student");

            if (student == null) {
                log.warn("START QUIZ FAILED: No student found in session.");
                log.warn("Redirecting user to /read/students/select");

                return "redirect:/read/students/select";
            }

            log.info("Student found in session. Student ID: {}", student.getId());
            log.info("Student parent: {}", student.getParent());
            log.info("Student class level: {}", student.getClasslevel());


            // ---------------------------------------------------------
            // 3. Verify student belongs to logged-in user
            // ---------------------------------------------------------
            log.info(
                    "Checking student ownership. Student parent: {}, Logged-in user: {}",
                    student.getParent(),
                    user.getUserId()
            );

            if (!student.getParent().equals(user.getUserId())) {

                log.warn(
                        "START QUIZ FAILED: Student {} does not belong to user {}",
                        student.getId(),
                        user.getUserId()
                );

                request.getSession()
                        .removeAttribute("student");

                log.info("Removed invalid student from session.");
                log.info("Redirecting to /read/students/select");

                return "redirect:/read/students/select";
            }

            log.info("Student ownership check PASSED.");


            // ---------------------------------------------------------
            // 4. Verify class level
            // ---------------------------------------------------------
            log.info(
                    "Checking class level. URL classLevelId: {}, Student classLevel: {}",
                    classLevelId,
                    student.getClasslevel()
            );

            if (!student.getClasslevel().equals(classLevelId)) {

                log.warn(
                        "START QUIZ FAILED: Class level mismatch. URL={}, Student={}",
                        classLevelId,
                        student.getClasslevel()
                );

                log.info("Redirecting to /read/webquiz");

                return "redirect:/read/webquiz";
            }

            log.info("Class level security check PASSED.");


            // ---------------------------------------------------------
            // 5. Verify subject is valid for class
            // ---------------------------------------------------------
            log.info(
                    "Checking whether subject {} is valid for class level {}",
                    subjectId,
                    classLevelId
            );

            Optional<SubjectLevel> subjectLevel =
                    subjectLevelRepository
                            .findValidSubjectLevel(
                                    subjectId,
                                    classLevelId
                            );

            log.info(
                    "SubjectLevel repository lookup completed. Found: {}",
                    subjectLevel.isPresent()
            );

            if (subjectLevel.isEmpty()) {

                log.warn(
                        "START QUIZ FAILED: Subject {} is not available for class level {}",
                        subjectId,
                        classLevelId
                );

                log.info("Redirecting to /read/webquiz");

                return "redirect:/read/webquiz";
            }

            log.info(
                    "Subject validation PASSED. SubjectLevel: {}",
                    subjectLevel.get()
            );


            // ---------------------------------------------------------
            // 6. Create quiz
            // ---------------------------------------------------------
            log.info(
                    "Attempting to create quiz. Student={}, ClassLevel={}, Subject={}, Questions={}",
                    student.getId(),
                    classLevelId,
                    subjectId,
                    20
            );

            QuizDone quiz =
                    webQuizService.createQuiz(
                            student.getId(),
                            classLevelId,
                            subjectId,
                            20
                    );

            log.info("Quiz creation method returned successfully.");

            if (quiz == null) {
                log.error("START QUIZ FAILED: createQuiz() returned NULL.");

                return "redirect:/read/webquiz";
            }

            log.info("Quiz created successfully.");
            log.info("Quiz ID: {}", quiz.id);


            // ---------------------------------------------------------
            // 7. Build redirect URL
            // ---------------------------------------------------------
            String redirectUrl =
                    "redirect:/read/webquiz/play/"
                            + quiz.id
                            + "?question=1";

            log.info("Redirecting student to: {}", redirectUrl);
            log.info("========== START QUIZ SUCCESS ==========");

            return redirectUrl;

        } catch (Exception e) {

            // ---------------------------------------------------------
            // 8. Catch unexpected failure
            // ---------------------------------------------------------
            log.error(
                    "========== START QUIZ EXCEPTION =========="
            );

            log.error(
                    "Unexpected error while starting quiz. " +
                            "classLevelId={}, subjectId={}",
                    classLevelId,
                    subjectId,
                    e
            );

            return "redirect:/read/webquiz";
        }
    }



    /**
     * Display a quiz question.
     */
    @GetMapping("/play/{quizId}")
    public String playQuiz(
            @PathVariable Long quizId,
            @RequestParam(
                    defaultValue = "1"
            ) int question,
            HttpServletRequest request,
            Model model
    ) {

        LoginSession user =
                (LoginSession) request.getSession()
                        .getAttribute("user");

        if (user == null) {
            return "redirect:/read/login";
        }


        IbukaStudentAccount student =
                (IbukaStudentAccount) request.getSession()
                        .getAttribute("student");

        if (student == null) {
            return "redirect:/read/students/select";
        }


        /*
         * Security check.
         */
        if (!student.getParent().equals(user.getUserId())) {

            request.getSession()
                    .removeAttribute("student");

            return "redirect:/read/students/select";
        }


        /*
         * Make sure the quiz belongs to the currently
         * selected student.
         */
        QuizDone quiz =
                webQuizService.getStudentQuiz(
                        quizId,
                        student.getId()
                );


        if (quiz == null) {
            return "redirect:/read/webquiz";
        }


        /*
         * If the quiz is already completed, don't allow
         * the student to continue answering it.
         */
        if (quiz.endDate != null) {
            return "redirect:/read/webquiz/result/"
                    + quizId;
        }


        /*
         * Validate question number.
         */
        List<QuizQuestion> quizQuestions =
                webQuizService.getQuizQuestions(
                        quizId
                );


        if (quizQuestions.isEmpty()) {
            return "redirect:/read/webquiz";
        }


        if (question < 1 ||
                question > quizQuestions.size()) {

            return "redirect:/read/webquiz/play/"
                    + quizId
                    + "?question=1";
        }


        WebQuizQuestionDTO currentQuestion =
                webQuizService.getQuestion(
                        quizId,
                        question
                );


        model.addAttribute(
                "quiz",
                quiz
        );

        model.addAttribute(
                "currentQuestion",
                currentQuestion
        );

        model.addAttribute(
                "questionNumber",
                question
        );

        model.addAttribute(
                "totalQuestions",
                quizQuestions.size()
        );

        model.addAttribute(
                "student",
                student
        );


        return "read/webquiz/play";
    }


    /**
     * Save the student's answer and move to
     * the next question.
     */
    @PostMapping("/answer/{quizId}")
    public String answerQuestion(
            @PathVariable Long quizId,
            @RequestParam Long questionId,
            @RequestParam(required = false)
            Long selectedChoice,
            @RequestParam int question,
            HttpServletRequest request
    ) {

        LoginSession user =
                (LoginSession) request.getSession()
                        .getAttribute("user");

        if (user == null) {
            return "redirect:/read/login";
        }


        IbukaStudentAccount student =
                (IbukaStudentAccount) request.getSession()
                        .getAttribute("student");

        if (student == null) {
            return "redirect:/read/students/select";
        }


        /*
         * Security check.
         */
        if (!student.getParent().equals(user.getUserId())) {

            request.getSession()
                    .removeAttribute("student");

            return "redirect:/read/students/select";
        }


        /*
         * Make sure the quiz belongs to this student.
         */
        QuizDone quiz =
                webQuizService.getStudentQuiz(
                        quizId,
                        student.getId()
                );


        if (quiz == null) {
            return "redirect:/read/webquiz";
        }


        /*
         * Don't allow answers after completion.
         */
        if (quiz.endDate != null) {
            return "redirect:/read/webquiz/result/"
                    + quizId;
        }


        /*
         * Save the answer.
         *
         * The service itself determines whether the
         * selected choice is correct.
         */
        if (selectedChoice != null) {

            webQuizService.answerQuestion(
                    quizId,
                    questionId,
                    selectedChoice
            );
        }


        /*
         * Determine whether this was the final question.
         */
        List<QuizQuestion> quizQuestions =
                webQuizService.getQuizQuestions(
                        quizId
                );


        if (question >= quizQuestions.size()) {

            webQuizService.finishQuiz(
                    quizId
            );

            return "redirect:/read/webquiz/result/"
                    + quizId;
        }


        /*
         * Move to next question.
         */
        return "redirect:/read/webquiz/play/"
                + quizId
                + "?question="
                + (question + 1);
    }


    /**
     * Display quiz results.
     */
    @GetMapping("/result/{quizId}")
    public String result(
            @PathVariable Long quizId,
            HttpServletRequest request,
            Model model
    ) {

        LoginSession user =
                (LoginSession) request.getSession()
                        .getAttribute("user");

        if (user == null) {
            return "redirect:/read/login";
        }


        IbukaStudentAccount student =
                (IbukaStudentAccount) request.getSession()
                        .getAttribute("student");

        if (student == null) {
            return "redirect:/read/students/select";
        }


        /*
         * Security check.
         */
        if (!student.getParent().equals(user.getUserId())) {

            request.getSession()
                    .removeAttribute("student");

            return "redirect:/read/students/select";
        }


        /*
         * Make sure this quiz belongs to this student.
         */
        QuizDone quiz =
                webQuizService.getStudentQuiz(
                        quizId,
                        student.getId()
                );


        if (quiz == null) {
            return "redirect:/read/webquiz";
        }


        /*
         * If somehow the quiz hasn't been completed,
         * finish it before showing results.
         */
        if (quiz.endDate == null) {

            quiz =
                    webQuizService.finishQuiz(
                            quizId
                    );
        }


        List<WebQuizQuestionDTO> results =
                webQuizService.getResults(
                        quizId
                );


        model.addAttribute(
                "quiz",
                quiz
        );

        model.addAttribute(
                "results",
                results
        );

        model.addAttribute(
                "student",
                student
        );


        return "read/webquiz/result";
    }
}