package ke.co.myfuture.Myfuture.UserManagement.ReportsAndMarketing.WeeklyStudentReports;

import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDone;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDoneRepository;
import ke.co.myfuture.Myfuture.UserManagement.Studentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.Studentaccount.StudentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class WeeklyAnalysisService {
    private final QuizDoneRepository quizDoneRepository;

    @Autowired
    StudentAccountRepository studentAccountRepository;
    @Autowired
    SubjectRepository subjectRepository;

    private Map<Long, String> subjectCache = new ConcurrentHashMap<>();

    public WeeklyAnalysisService(QuizDoneRepository quizDoneRepository) {
        this.quizDoneRepository = quizDoneRepository;
    }

    // Analysis for a single student
    public WeeklyAnalysisDto getWeeklyAnalysis(IbukaStudentAccount student, LocalDateTime startOfWeek, LocalDateTime endOfWeek) {
        Optional<IbukaStudentAccount> studentAccount = studentAccountRepository.findById(student.getId());
        if (studentAccount.isEmpty()) return null;
        List<QuizDone> quizzes = quizDoneRepository.findQuizzesByStudentAndWeek(studentAccount.get(), startOfWeek, endOfWeek);

        Map<Long, List<QuizDone>> groupedBySubject = quizzes.stream().collect(Collectors.groupingBy(QuizDone::getSubjectId));

        List<SubjectAnalysisDto> subjectAnalysis = new ArrayList<>();
        int totalWeekScore = 0;

        for (Map.Entry<Long, List<QuizDone>> entry : groupedBySubject.entrySet()) {
            Long subjectId = entry.getKey();
            List<QuizDone> subjectQuizzes = entry.getValue();

            int totalScore = subjectQuizzes.stream().mapToInt(QuizDone::getScore).sum();
            int totalOverall = subjectQuizzes.stream().mapToInt(QuizDone::getOverall).sum();
            double percentage = totalOverall > 0 ? ((double) totalScore / totalOverall) * 100 : 0;

            subjectAnalysis.add(new SubjectAnalysisDto(subjectId, getSubject(subjectId), totalScore, percentage));
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

        for (IbukaStudentAccount student : studentIds) {
            System.out.println("-----------------------------------------------------------");
            WeeklyAnalysisDto report = getWeeklyAnalysis(student, startOfWeek, endOfWeek);
            // Persist or log the report
            if (report.getTotalWeekScore() > 0){
                String emailContent = generateHtmlEmailContent(student, report, startOfWeek, endOfWeek);
                System.out.println(emailContent);
//                System.out.println("Weekly Report for Student ID " + student + ": " + report);
            }else {
                System.out.println("Ignoring, score is zero");
            }
        }
    }

    private String generateHtmlEmailContent(IbukaStudentAccount student, WeeklyAnalysisDto report, LocalDateTime startOfWeek, LocalDateTime endOfWeek) {
        StringBuilder emailContent = new StringBuilder();

        emailContent.append("<html>");
        emailContent.append("<body style='font-family: Arial, sans-serif;'>");
        emailContent.append("<h2>Weekly Score Report</h2>");
        emailContent.append("<p>Hello ").append(student.getName()).append(",</p>");
        emailContent.append("<p>Here is your performance summary for the week of <b>")
                .append(startOfWeek.toLocalDate()).append("</b> to <b>").append(endOfWeek.toLocalDate()).append("</b>:</p>");
        emailContent.append("<h3>Total Weekly Score: ").append(report.getTotalWeekScore()).append("</h3>");

        emailContent.append("<table border='1' cellspacing='0' cellpadding='8' style='border-collapse: collapse; width: 100%;'>");
        emailContent.append("<tr style='background-color: #f2f2f2;'>");
        emailContent.append("<th>Subject</th>");
        emailContent.append("<th>Total Score</th>");
        emailContent.append("<th>Percentage</th>");
        emailContent.append("</tr>");

        for (SubjectAnalysisDto subject : report.getSubjects()) {
            emailContent.append("<tr>");
            emailContent.append("<td>").append(subject.getSubjectName()).append("</td>");
            emailContent.append("<td>").append(subject.getTotalScore()).append("</td>");
            emailContent.append("<td>").append(String.format("%.2f", subject.getPercentage())).append("%</td>");
            emailContent.append("</tr>");
        }

        emailContent.append("</table>");
        emailContent.append("<p>Keep up the good work!</p>");
        emailContent.append("<p>Best regards,<br>Your Future Team</p>");
        emailContent.append("</body>");
        emailContent.append("</html>");

        return emailContent.toString();
    }

//    @Scheduled(cron = "0 0 0 ? * SUN")
    @Bean
    public void generateReportsForPreviousWeek() {
        System.out.println("Starting weekly report generation at: " + LocalDateTime.now());
        generateWeeklyReports();
    }
}
