package ke.co.myfuture.Myfuture.Commonauth.Install;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDoneRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Objects;

@Controller
@RequestMapping("/admin/android-versions")
@AllArgsConstructor
public class AdminAndroidVersionAnalysisController {
    private static final String ANDROID_PLATFORM = "myfuture_android";
    private static final int MAX_VERSIONS = 20;

    private final Install2Repository installRepository;
    private final IbukaStudentAccountRepository studentRepository;
    private final QuizDoneRepository quizDoneRepository;

    @GetMapping
    public String list(Model model) {
        List<Map<String, Object>> analysis = new ArrayList<>();
        List<Integer> versions = installRepository.findAndroidVersionsByPlatform(
                ANDROID_PLATFORM, PageRequest.of(0, MAX_VERSIONS));

        for (Integer version : versions) {
            List<Install> installs = installRepository.findTop200ByPlatformAndVersionOrderByCreatedAtDesc(
                    ANDROID_PLATFORM, version);
            if (installs.isEmpty()) continue;

            Date earliestInstall = installs.stream()
                    .map(Install::getCreatedAt)
                    .filter(Objects::nonNull)
                    .min(Date::compareTo)
                    .orElse(null);
            List<Long> installIds = installs.stream().map(Install::getId).toList();
            List<Long> accountIds = installs.stream()
                    .map(Install::getAccountId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            long usersWithStudents = accountIds.isEmpty() ? 0
                    : studentRepository.countDistinctParentsByParentIn(accountIds);
            long studentQuizzes = accountIds.isEmpty() ? 0
                    : quizDoneRepository.countActiveByStudentParentIn(accountIds);
            long quizzesWithoutStudent = quizDoneRepository.countActiveWithoutStudentByInstallIdIn(installIds);

            analysis.add(Map.of(
                    "version", version,
                    "earliestInstall", earliestInstall == null ? "-"
                            : new SimpleDateFormat("yyyy-MM-dd HH:mm").format(earliestInstall),
                    "installCount", installs.size(),
                    "userCount", accountIds.size(),
                    "usersWithStudents", usersWithStudents,
                    "studentQuizzes", studentQuizzes,
                    "quizzesWithoutStudent", quizzesWithoutStudent
            ));
        }

        model.addAttribute("analysis", analysis);
        model.addAttribute("maxVersions", MAX_VERSIONS);
        return "admin/android_version_analysis";
    }
}
