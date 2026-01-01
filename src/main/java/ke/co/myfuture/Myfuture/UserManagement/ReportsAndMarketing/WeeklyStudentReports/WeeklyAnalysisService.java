package ke.co.myfuture.Myfuture.UserManagement.ReportsAndMarketing.WeeklyStudentReports;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDone;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDoneRepository;
import ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails.*;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class WeeklyAnalysisService {
    private final QuizDoneRepository quizDoneRepository;

    @Autowired
    IbukaStudentAccountRepository ibukaStudentAccountRepository;
    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SchedulerService schedulerService;

    @Value("${production}")
    private boolean inProd;

    private Map<Long, String> subjectCache = new ConcurrentHashMap<>();

    public WeeklyAnalysisService(QuizDoneRepository quizDoneRepository) {
        this.quizDoneRepository = quizDoneRepository;
    }

    // Analysis for a single student
    public WeeklyAnalysisDto getWeeklyAnalysis(IbukaStudentAccount student, LocalDateTime startOfWeek, LocalDateTime endOfWeek) {
        Optional<IbukaStudentAccount> studentAccount = ibukaStudentAccountRepository.findById(student.getId());
        if (studentAccount.isEmpty()) return null;
        ZoneId zoneId = ZoneId.systemDefault();
        Date startDate = Date.from(startOfWeek.atZone(zoneId).toInstant());
        Date endDate   = Date.from(endOfWeek.atZone(zoneId).toInstant());
        List<QuizDone> quizzes = quizDoneRepository.findQuizzesByStudentAndWeek(studentAccount.get(), startDate, endDate);

        Map<Long, List<QuizDone>> groupedBySubject = quizzes.stream().collect(Collectors.groupingBy(QuizDone::getSubjectId));

        List<SubjectAnalysisDto> subjectAnalysis = new ArrayList<>();
        int totalWeekScore = 0;

        for (Map.Entry<Long, List<QuizDone>> entry : groupedBySubject.entrySet()) {
            Long subjectId = entry.getKey();
            List<QuizDone> subjectQuizzes = entry.getValue();

            int totalScore = subjectQuizzes.stream().mapToInt(QuizDone::getScore).sum();
            int totalOverall = subjectQuizzes.stream().mapToInt(QuizDone::getOverall).sum();
            double percentage = totalOverall > 0 ? ((double) totalScore / totalOverall) * 100 : 0;

            subjectAnalysis.add(new SubjectAnalysisDto(subjectId, getSubject(subjectId), totalScore, totalOverall, percentage));
            totalWeekScore += totalScore;
        }

        subjectAnalysis.sort(Comparator.comparingDouble(SubjectAnalysisDto::getPercentage).reversed());

        return new WeeklyAnalysisDto(totalWeekScore, subjectAnalysis);
    }

    private String getSubject(Long subjectId) {
        // Check if the name is already in the cache
        if (subjectCache.containsKey(subjectId)) {
            System.out.println("Is contained in cache");
            return subjectCache.get(subjectId);
        }

        // If not in cache, query the database and cache the result
        String subjectName = subjectRepository.getName(subjectId);

        System.out.println("From db "+subjectName+" for id "+subjectId);

        // Cache the name if found
        if (subjectName != null) {
            subjectCache.put(subjectId, subjectName);
        }

        System.out.println("Subject to return: "+subjectName);
        return subjectName;
    }

    // Run analysis for all students
    public void generateWeeklyReports() {
        LocalDateTime endOfWeek = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SATURDAY)).withHour(23).withMinute(59).withSecond(59);
        LocalDateTime startOfWeek = endOfWeek.minusDays(6).withHour(0).withMinute(0).withSecond(0);


        System.out.println("startOfWeek: "+startOfWeek+", endOfWeek: "+endOfWeek);

        // Fetch all unique student IDs
        List<IbukaStudentAccount> studentIds = quizDoneRepository.findDistinctStudentIds();

        User parentUser;
        int count = 0;
        for (IbukaStudentAccount student : studentIds) {
            System.out.println("-----------------------------------------------------------");
            WeeklyAnalysisDto report = getWeeklyAnalysis(student, startOfWeek, endOfWeek);
            // Persist or log the report
            if (report.getTotalWeekScore() > 0) {
                count++;
                parentUser = userRepository.findById(student.getParent()).get();
                student.setAuthUser(parentUser);
                String emailContent = generateHtmlEmailContent(student, report, startOfWeek, endOfWeek);
                System.out.println(emailContent);

                if (!inProd) return;
                schedulerService.persistScheduledEmail(parentUser.getEmail(), "Myfuture CBC Revision Weekly Score: "+student.getName(), emailContent, "Ibuka Technologies", LocalDateTime.now().plusMinutes(count/3), SenderService.WeeklyScore);
//                System.out.println("Weekly Report for Student ID " + student + ": " + report);
            }else {
                System.out.println("Ignoring, score is zero");
            }
        }
    }

    private String generateHtmlEmailContent(IbukaStudentAccount student, WeeklyAnalysisDto report, LocalDateTime startOfWeek, LocalDateTime endOfWeek) {
        StringBuilder emailContent = new StringBuilder();

        emailContent.append("<html>");
        emailContent.append("<body style='font-family: Arial, sans-serif; margin: 0; padding: 0;'>");

        // Header Section
        emailContent.append("<div style='background-color: #007BFF; color: white; padding: 20px; text-align: center;'>");
        emailContent.append("<h1 style='margin: 0; font-size: 24px;'>Myfuture CBC Revision</h1>");
        emailContent.append("<p style='color: maroon; margin: 0; font-size: 16px;'>Your partner in academic excellence</p>");
        emailContent.append("</div>");

        // Main Content Wrapper
        emailContent.append("<div style='padding: 20px;'>");
        emailContent.append("<h2 style='color: #007BFF; font-size: 20px; margin-bottom: 10px;'>Weekly Student Score Report</h2>");
        emailContent.append("<p style='margin: 0; font-size: 16px;'>Hello ").append(student.getName()).append(",</p>");
        emailContent.append("<p style='margin: 10px 0 20px; font-size: 16px;'>Here is your performance summary for the week of <b>")
                .append(startOfWeek.toLocalDate()).append("</b> to <b>").append(endOfWeek.toLocalDate()).append("</b>:</p>");
        emailContent.append("<h3 style='color: #007BFF; margin-bottom: 20px;'>Total Weekly Score: ").append(report.getTotalWeekScore()).append("</h3>");

        // Table for subjects
        emailContent.append("<table border='1' cellspacing='0' cellpadding='8' style='border-collapse: collapse; width: 100%;'>");
        emailContent.append("<tr style='background-color: #f2f2f2; text-align: left;'>");
        emailContent.append("<th style='padding: 10px;'>Subject</th>");
        emailContent.append("<th style='padding: 10px;'>Total Score</th>");
        emailContent.append("<th style='padding: 10px;'>Overall Score</th>");
        emailContent.append("<th style='padding: 10px;'>Percentage</th>");
        emailContent.append("</tr>");

        for (SubjectAnalysisDto subject : report.getSubjects()) {
            emailContent.append("<tr>");
            emailContent.append("<td style='padding: 10px;'>").append(subject.getSubjectName()).append("</td>");
            emailContent.append("<td style='padding: 10px;'>").append(subject.getTotalScore()).append("</td>");
            emailContent.append("<td style='padding: 10px;'>").append(subject.getOverAllScore()).append("</td>");
            emailContent.append("<td style='padding: 10px;'>").append(String.format("%.2f", subject.getPercentage())).append("%</td>");
            emailContent.append("</tr>");
        }

        emailContent.append("</table>");

        // Closing Message
        emailContent.append("<p style='margin-top: 20px; font-size: 16px;'>Keep striving for excellence, and remember that consistent effort leads to success!</p>");
        emailContent.append("<p style='margin-top: 20px; font-size: 16px;'>Best regards,<br><b>The Myfuture CBC Revision Team</b></p>");
        emailContent.append("</div>");

        emailContent.append("</body>");
        emailContent.append("</html>");

        return emailContent.toString();
    }

    @Scheduled(cron = "0 0 0 ? * SUN")
    public void generateReportsForPreviousWeek() {
        System.out.println("generateReportsForPreviousWeek");
        System.out.println("Starting weekly report generation at: " + LocalDateTime.now());
        generateWeeklyReports();
    }

    @Scheduled(cron = "0 0 1 ? * FRI")
//    @Bean
    public void rescueTheWeek() {
        if (!inProd) return;
        System.out.println("Starting rescue week generation at: " + LocalDateTime.now());


        LocalDateTime endOfWeek = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SATURDAY)).withHour(23).withMinute(59).withSecond(59);
        LocalDateTime startOfWeek = endOfWeek.minusDays(6).withHour(0).withMinute(0).withSecond(0);
//        LocalDateTime startOfWeek = endOfWeek.minusDays(200).withHour(0).withMinute(0).withSecond(0);

        System.out.println("startOfWeek: "+startOfWeek+", endOfWeek: "+endOfWeek);

        // Fetch all unique student IDs
        List<IbukaStudentAccount> studentIds = quizDoneRepository.findDistinctStudentIds();

        User parentUser;
        int count = 0;
        for (IbukaStudentAccount student : studentIds) {
            System.out.println("-----------------------------------------------------------");
            // Persist or log the report

            parentUser = userRepository.findById(student.getParent()).get();
            student.setAuthUser(parentUser);
            String emailContent = generateRescueWeekEmailContent(student, endOfWeek);
            System.out.println(emailContent);

            schedulerService.persistScheduledEmail(parentUser.getEmail(), "Myfuture CBC Revision: Rescue Week", emailContent,"Ibuka Technologies", LocalDateTime.now().plusMinutes(count/3), SenderService.WeeklyScore);

        }
    }

    private String generateRescueWeekEmailContent(IbukaStudentAccount student, LocalDateTime endOfWeek) {
        StringBuilder emailContent = new StringBuilder();

        emailContent.append("<html>");
        emailContent.append("<body style='font-family: Arial, sans-serif; margin: 0; padding: 0;'>");

        // Header Section
        emailContent.append("<div style='background-color: #28a745; color: white; padding: 20px; text-align: center;'>");
        emailContent.append("<h1 style='margin: 0; font-size: 24px;'>Myfuture CBC Revision</h1>");
        emailContent.append("<p style='color: yellow; margin: 0; font-size: 16px;'>Your partner in academic excellence</p>");
        emailContent.append("</div>");

        // Main Content Wrapper
        emailContent.append("<div style='padding: 20px;'>");

        emailContent.append("<h2 style='color: #28a745; font-size: 20px; margin-bottom: 10px;'>Rescue the Week - You Can Do It!</h2>");

        emailContent.append("<p style='margin: 0; font-size: 16px;'>Hello ").append(student.getName()).append(",</p>");

        emailContent.append("<p style='margin: 10px 0 20px; font-size: 16px;'>Just a friendly reminder: there are still a few days left before the end of the week (<b>")
                .append(endOfWeek.toLocalDate()).append("</b>). This is your chance to rescue the week! Give your best effort, and you can still improve your scores and make amazing progress!</p>");

        // Motivational Section
        emailContent.append("<div style='background-color: #f2f2f2; padding: 20px; margin-top: 20px; border-radius: 8px; text-align: center;'>");
        emailContent.append("<h3 style='color: #28a745; margin: 0;'>Remember:</h3>");
        emailContent.append("<p style='font-size: 16px; margin-top: 10px;'>");
        emailContent.append("🌟 Every little effort counts!<br>");
        emailContent.append("📚 Learning is your superpower!<br>");
        emailContent.append("💪 You can do anything you set your mind to!");
        emailContent.append("</p>");
        emailContent.append("</div>");

        // Action Call
        emailContent.append("<p style='margin-top: 20px; font-size: 16px; text-align: center;'>");
        emailContent.append("Complete your lessons, take on those quizzes, and show everyone how brilliant you are!");
        emailContent.append("</p>");

        // Closing Message
        emailContent.append("<p style='margin-top: 20px; font-size: 16px;'>");
        emailContent.append("We believe in you! Let’s finish the week strong! 🎯</p>");

        emailContent.append("<p style='margin-top: 20px; font-size: 16px;'>");
        emailContent.append("Best regards,<br><b>The Myfuture CBC Revision Team</b></p>");

        emailContent.append("</div>");

        emailContent.append("</body>");
        emailContent.append("</html>");

        return emailContent.toString();
    }
}
