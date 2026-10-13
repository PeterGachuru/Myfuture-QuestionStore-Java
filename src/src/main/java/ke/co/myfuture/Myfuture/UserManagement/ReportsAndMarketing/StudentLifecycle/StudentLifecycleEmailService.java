package ke.co.myfuture.Myfuture.UserManagement.ReportsAndMarketing.StudentLifecycle;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails.SchedulerService;
import ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails.ScheduledEmailsRepo;
import ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails.SenderService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDone;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDoneRepository;
import ke.co.myfuture.Myfuture.UserManagement.StudySubscription.StudySubscription;
import ke.co.myfuture.Myfuture.UserManagement.StudySubscription.StudySubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Student lifecycle email campaigns. Emails are queued through the existing
 * ScheduledEmails mechanism; EmailSchedulerService remains responsible for delivery.
 */
@Service
public class StudentLifecycleEmailService {
    private static final Logger log = LoggerFactory.getLogger(StudentLifecycleEmailService.class);
    private static final String PROFILE_URL = "https://study.myfuture.co.ke/read/myprofile";
    private static final String APP_URL = "https://play.google.com/store/apps/details?id=ke.co.myfuture";
    private static final String FAVICON_URL = "https://myfuture.co.ke/favicon.ico";
    private static final String BRAND = "MyFuture CBC Revision";

    @Autowired private IbukaStudentAccountRepository studentRepository;
    @Autowired private QuizDoneRepository quizDoneRepository;
    @Autowired private StudySubscriptionRepository subscriptionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CurriLevelRepository curriLevelRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private SchedulerService schedulerService;
    @Autowired private ScheduledEmailsRepo scheduledEmailsRepo;

    @Value("${student.lifecycle.email.enabled:true}")
    private boolean lifecycleEmailEnabled;

    /**
     * Poll frequently so newly eligible campaigns are queued promptly. This controls
     * queue creation only; EmailSchedulerService controls actual delivery separately.
     */
    @Scheduled(initialDelay = 0, fixedDelayString = "${student.lifecycle.email.scan-delay-ms:3600000}")
    public void processStudentLifecycleEmails() {
        if (!lifecycleEmailEnabled) {
            log.debug("Student lifecycle email scan skipped: student.lifecycle.email.enabled=false");
            return;
        }

        Date now = new Date();
        int pageNumber = 0;
        long scanned = 0;
        long failed = 0;
        log.info("Student lifecycle email scan started");
        Page<IbukaStudentAccount> page;
        do {
            page = studentRepository.findAll(
                    PageRequest.of(pageNumber, 250, Sort.by(Sort.Direction.ASC, "id")));
            for (IbukaStudentAccount student : page.getContent()) {
                scanned++;
                try {
                    processStudent(student, now);
                } catch (Exception ex) {
                    failed++;
                    // One student's bad/incomplete record must not stop the campaign.
                    log.error("Student lifecycle email processing failed for studentId={}", student.getId(), ex);
                }
            }
            pageNumber++;
        } while (page.hasNext());
        log.info("Student lifecycle email scan completed: studentsScanned={}, failures={}", scanned, failed);
    }

    private void processStudent(IbukaStudentAccount student, Date now) {
        if (student.getParent() == null) {
            log.debug("Lifecycle skip studentId={}: parent user id is null", student.getId());
            return;
        }

        Optional<User> parentOpt = userRepository.findById(student.getParent());
        if (parentOpt.isEmpty()) {
            log.debug("Lifecycle skip studentId={}: no parent User found for parentId={}", student.getId(), student.getParent());
            return;
        }
        User parent = parentOpt.get();
        String recipient = parent.getEmail();
        if (!SchedulerService.isValidEmail(recipient)) {
            log.debug("Lifecycle skip studentId={}: parent email is missing or invalid", student.getId());
            return;
        }
        log.debug("Evaluating lifecycle campaigns for studentId={}, createdAt={}, recentActivity={}, parentEmail={}",
                student.getId(), student.getCreatedAt(), student.getRecentActivity(), maskEmail(recipient));

        String studentName = firstNonBlank(student.getFirstName(), student.getName(), "there");
        Date createdAt = student.getCreatedAt();
        Date dayAgo = daysAgo(now, 1);
        Date sevenDaysAgo = daysAgo(now, 7);

        Optional<QuizDone> firstQuiz = quizDoneRepository
                .findFirstByStudentAndDeletedFalseOrderByCreatedAtAsc(student);
        Optional<QuizDone> lastQuiz = quizDoneRepository
                .findFirstByStudentAndDeletedFalseOrderByCreatedAtDesc(student);
        boolean hasQuiz = firstQuiz.isPresent();

        // 1. Welcome: queue once for recently registered students.
        if (createdAt != null && !createdAt.before(sevenDaysAgo)) {
            log.debug("Lifecycle trigger WELCOME studentId={}: createdAt={} is within last 7 days", student.getId(), createdAt);
            queueOnce(recipient, "Welcome to MyFuture, " + safeSubjectName(studentName),
                    welcomeEmail(student, studentName), "LifecycleWelcome", "WELCOME:" + student.getId(),
                    new Date(0), now);
        }

        // 2. First-quiz celebration: send soon after the first completed quiz.
        if (firstQuiz.isPresent() && firstQuiz.get().getCreatedAt() != null
                && !firstQuiz.get().getCreatedAt().before(dayAgo)) {
            log.debug("Lifecycle trigger FIRST_QUIZ studentId={}: first quiz at {}", student.getId(), firstQuiz.get().getCreatedAt());
            queueOnce(recipient, "Well done on your first MyFuture quiz, " + safeSubjectName(studentName),
                    firstQuizEmail(student, studentName), "LifecycleFirstQuizCompleted", "FIRST_QUIZ:" + student.getId(),
                    new Date(0), now);
        }

        // 3. Registered, but has not attempted a quiz yet.
        if (!hasQuiz && createdAt != null
                && createdAt.before(daysAgo(now, 1))
                && !createdAt.before(daysAgo(now, 14))) {
            log.debug("Lifecycle trigger NO_QUIZ studentId={}: account age is between 1 and 14 days", student.getId());
            queueOnce(recipient, "Your first MyFuture quiz is waiting, " + safeSubjectName(studentName),
                    firstQuizNudgeEmail(student, studentName), "LifecycleFirstQuizReminder", "NO_QUIZ:" + student.getId(),
                    new Date(0), now);
        }

        Date lastStudy = mostRecent(student.getRecentActivity(),
                lastQuiz.map(QuizDone::getCreatedAt).orElse(null), createdAt);
        log.debug("Lifecycle eligibility studentId={}: createdAt={}, recentActivity={}, firstQuizAt={}, lastQuizAt={}, hasQuiz={}, lastStudy={}",
                student.getId(), createdAt, student.getRecentActivity(),
                firstQuiz.map(QuizDone::getCreatedAt).orElse(null),
                lastQuiz.map(QuizDone::getCreatedAt).orElse(null), hasQuiz, lastStudy);

        if (lastStudy != null) {
            long inactiveDays = (now.getTime() - lastStudy.getTime()) / (24L * 60 * 60 * 1000);
            if (inactiveDays >= 30) {
                log.debug("Lifecycle trigger INACTIVE_MONTHLY studentId={}: inactiveDays={}, hasQuiz={}", student.getId(), inactiveDays, hasQuiz);
                // Long-term reactivation includes students who never completed a quiz.
                // Same subject + 30-day duplicate window means one reminder per shared inbox per month.
                queueOnce(recipient, "A fresh revision plan from MyFuture",
                        longInactiveEmail(student, studentName, inactiveDays, hasQuiz),
                        "LifecycleInactiveMonthly", "INACTIVE_MONTHLY:" + student.getId(), daysAgo(now, 30), now);
            } else if (hasQuiz && inactiveDays >= 7) {
                log.debug("Lifecycle trigger INACTIVE_7 studentId={}: inactiveDays={}", student.getId(), inactiveDays);
                queueOnce(recipient, "Let's get back to revision, " + safeSubjectName(studentName),
                        inactiveEmail(student, studentName, true), "LifecycleInactive7Days", "INACTIVE_7:" + student.getId(),
                        daysAgo(now, 30), now);
            } else if (hasQuiz && inactiveDays >= 3) {
                log.debug("Lifecycle trigger INACTIVE_3 studentId={}: inactiveDays={}", student.getId(), inactiveDays);
                queueOnce(recipient, "A quick revision challenge for " + safeSubjectName(studentName),
                        inactiveEmail(student, studentName, false), "LifecycleInactive3Days", "INACTIVE_3:" + student.getId(),
                        daysAgo(now, 30), now);
            }
        }

        // 4. Encourage engaged students who do not currently have a subscription.
        boolean activeSubscription = hasActiveSubscription(student, now);
        log.debug("Lifecycle subscription eligibility studentId={}: activeSubscription={}, parentUsernamePresent={}",
                student.getId(), activeSubscription, student.getParentUsername() != null && !student.getParentUsername().isBlank());
        if (hasQuiz && !activeSubscription && lastStudy != null
                && lastStudy.before(daysAgo(now, 2))
                && !lastStudy.before(daysAgo(now, 30))) {
            log.debug("Lifecycle trigger SUBSCRIPTION_NUDGE studentId={}: no active subscription; lastStudy={}", student.getId(), lastStudy);
            queueOnce(recipient, "Keep your MyFuture revision going, " + safeSubjectName(studentName),
                    subscriptionNudgeEmail(student, studentName), "LifecycleSubscriptionNudge", "SUBSCRIPTION_NUDGE:" + student.getId(),
                    daysAgo(now, 30), now);
        }

        // 5. Notify the parent shortly before the latest active subscription ends.
        if (activeSubscription && student.getParentUsername() != null) {
            List<StudySubscription> subscriptions =
                    subscriptionRepository.findByEmailAddressAndEndDateAfterOrderByEndDateDesc(
                            student.getParentUsername(), now);
            if (!subscriptions.isEmpty()) {
                StudySubscription latest = subscriptions.get(0);
                Date expiry = latest.getEndDate();
                if (expiry != null && expiry.after(now) && expiry.before(daysFrom(now, 4))) {
                    log.debug("Lifecycle trigger EXPIRY studentId={}: expiry={}", student.getId(), expiry);
                    String subject = "MyFuture subscription expiry: " + safeSubjectName(studentName);
                    queueOnce(recipient, subject, expiryEmail(student, studentName, expiry),
                            "LifecycleSubscriptionExpiry", "EXPIRY:" + student.getId(), daysAgo(now, 7), now);
                }
            }
        }

        // 6. Confirmation after a new subscription is recorded.
        if (student.getParentUsername() != null) {
            List<StudySubscription> recentSubscriptions =
                    subscriptionRepository.findByEmailAddressAndCreatedAtAfterOrderByCreatedAtDesc(
                            student.getParentUsername(), dayAgo);
            if (!recentSubscriptions.isEmpty() && activeSubscription) {
                log.debug("Lifecycle trigger SUBSCRIBED studentId={}: recent subscription count={}", student.getId(), recentSubscriptions.size());
                queueOnce(recipient, "MyFuture subscription confirmed for " + safeSubjectName(studentName),
                        subscriptionConfirmedEmail(student, studentName),
                        "LifecycleSubscriptionConfirmed", "SUBSCRIBED:" + student.getId(), dayAgo, now);
            }
        }

        // 7. Weekly revision plan for students who have studied recently.
        if (java.time.LocalDate.now().getDayOfWeek() == java.time.DayOfWeek.MONDAY
                && hasQuiz && lastStudy != null && !lastStudy.before(sevenDaysAgo)) {
            log.debug("Lifecycle trigger WEEKLY_PLAN studentId={}: lastStudy={}", student.getId(), lastStudy);
            queueOnce(recipient, "Your weekly MyFuture revision plan: " + safeSubjectName(studentName),
                    weeklyPlanEmail(student, studentName), "LifecycleWeeklyRevisionPlan", "WEEKLY_PLAN:" + student.getId(),
                    daysAgo(now, 6), now);
        }
    }

    private boolean hasActiveSubscription(IbukaStudentAccount student, Date now) {
        if (student.getParentUsername() == null || student.getParentUsername().isBlank()) return false;
        return !subscriptionRepository
                .findByEmailAddressAndEndDateAfterOrderByEndDateDesc(student.getParentUsername(), now)
                .isEmpty();
    }

    private void queueOnce(String recipient, String subject, String html, String category, String campaignKey,
                           Date duplicateWindowStart, Date now) {
        // Campaign-specific deduplication prevents the hourly scanner from queuing the same
        // campaign repeatedly. The shared recipient cap prevents different campaigns (or
        // multiple children sharing one parent email) from flooding the same inbox.
        log.debug("Lifecycle email CREATE attempt: campaignKey={}, recipient={}, subject={}",
                campaignKey, maskEmail(recipient), subject);
        if (scheduledEmailsRepo.existsByRecipientAndSubjectAndCreatedAtAfter(
                recipient, subject, duplicateWindowStart)) {
            log.debug("Lifecycle email NOT queued: campaignKey={}, reason=duplicate recipient+subject in window starting {}",
                    campaignKey, duplicateWindowStart);
            return;
        }
        String normalizedRecipient = recipient.trim();
        long queuedOrSentInLastSevenDays = scheduledEmailsRepo.countByRecipientAndCreatedAtAfter(
                normalizedRecipient, daysAgo(now, 7));
        if (queuedOrSentInLastSevenDays >= 2) {
            log.debug("Lifecycle email NOT queued: campaignKey={}, reason=weekly recipient cap, countLast7Days={}",
                    campaignKey, queuedOrSentInLastSevenDays);
            return;
        }
        Boolean saved = schedulerService.persistScheduledEmail(normalizedRecipient, subject, html, "Ibuka Technologies",
                LocalDateTime.ofInstant(now.toInstant(), ZoneId.systemDefault()),
                category);
        if (Boolean.TRUE.equals(saved)) {
            log.info("Lifecycle email queued: category={}, campaignKey={}", category, campaignKey);
        } else {
            log.warn("Lifecycle email NOT queued: campaignKey={}, SchedulerService returned false (invalid recipient or persistence rejected)", campaignKey);
        }
    }

    private String welcomeEmail(IbukaStudentAccount student, String name) {
        String level = getClassLevelName(student);
        String subjects = getSubjects(student);
        return page("Welcome to MyFuture!",
                "<p>Hello " + esc(name) + ",</p>"
                + "<p>Welcome to MyFuture CBC Revision. Your learning journey starts with small, consistent steps.</p>"
                + (level.isEmpty() ? "" : "<p><b>Class:</b> " + esc(level) + "</p>")
                + (subjects.isEmpty() ? "" : "<p><b>Subjects available for your class:</b> " + esc(subjects) + "</p>")
                + "<p>Start with one short quiz today. Review the answers and revisit any topic that feels difficult.</p>"
                + button("Open MyFuture", PROFILE_URL)
                + "<p>You can also install the Android app and continue learning there.</p>"
                + button("Get the Android app", APP_URL));
    }

    private String firstQuizEmail(IbukaStudentAccount student, String name) {
        return page("Your first step is complete!",
                "<p>Well done, " + esc(name) + "!</p>"
                + "<p>You have completed your first quiz on MyFuture. The best way to improve is to keep practising and learn from every answer.</p>"
                + "<p>Try another quiz in the same subject, or explore another subject for your class.</p>"
                + button("Continue revising", PROFILE_URL));
    }

    private String firstQuizNudgeEmail(IbukaStudentAccount student, String name) {
        return page("Let's try your first quiz",
                "<p>Hello " + esc(name) + ",</p>"
                + "<p>Your MyFuture account is ready. Take a few minutes to attempt a quiz and see which topics you already understand.</p>"
                + "<p>You do not need a long study session to begin—start with a short activity and build from there.</p>"
                + button("Start revising", PROFILE_URL));
    }

    private String inactiveEmail(IbukaStudentAccount student, String name, boolean sevenDays) {
        String message = sevenDays
                ? "It has been a little while since your last recorded quiz. You can pick up again with a short revision session—there is no need to catch up all at once."
                : "A quick five-minute revision session can help keep what you learned fresh. Try a topic you have studied before.";
        return page(sevenDays ? "Ready to return to revision?" : "Your quick revision challenge",
                "<p>Hello " + esc(name) + ",</p><p>" + message + "</p>"
                + "<p>Choose a subject for your class and complete a short quiz today.</p>"
                + button("Resume learning", PROFILE_URL));
    }

    private String longInactiveEmail(IbukaStudentAccount student, String name, long inactiveDays, boolean hasQuiz) {
        long months = Math.max(1, inactiveDays / 30);
        String context = hasQuiz
                ? "It has been around " + months + " month(s) since your last recorded quiz. That is okay—you can restart at your own pace."
                : "It has been around " + months + " month(s) since this student account was created, and we have not recorded a completed quiz yet. You can start small and build a routine.";
        return page("A fresh start with MyFuture",
                "<p>Hello " + esc(name) + ",</p>"
                + "<p>" + esc(context) + "</p>"
                + "<p>Choose one subject for your class, revise a short topic, and try a few questions. You do not need to catch up everything at once.</p>"
                + button("Resume revision", PROFILE_URL)
                + "<p>You can also continue on Android. If you need full access, review the available plans and pay using M-Pesa from your profile; the subscription syncs with the app.</p>"
                + button("MyFuture Android app", APP_URL));
    }

    private String subscriptionNudgeEmail(IbukaStudentAccount student, String name) {
        return page("Keep learning with MyFuture",
                "<p>Hello " + esc(name) + ",</p>"
                + "<p>Keep your revision routine going with access to MyFuture learning resources.</p>"
                + "<p>You can review available subscription options and pay using M-Pesa from your profile. Your subscription will sync with the MyFuture Android app as well.</p>"
                + button("View subscription options", PROFILE_URL)
                + "<p>Prefer studying on your phone? Install or open the Android app.</p>"
                + button("MyFuture on Google Play", APP_URL)
                + "<p>If you already have an active subscription, you can ignore this message.</p>");
    }

    private String expiryEmail(IbukaStudentAccount student, String name, Date expiry) {
        String expiryDate = expiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString();
        return page("Your subscription is approaching expiry",
                "<p>Hello " + esc(name) + ",</p>"
                + "<p>Your current MyFuture subscription is scheduled to end on <b>" + esc(expiryDate) + "</b>.</p>"
                + "<p>If you would like to continue revising without interruption, visit your profile to review the available plans and pay using M-Pesa. The subscription syncs with the Android app too.</p>"
                + button("Manage subscription", PROFILE_URL)
                + button("Open Android app page", APP_URL));
    }

    private String subscriptionConfirmedEmail(IbukaStudentAccount student, String name) {
        return page("Your subscription is active",
                "<p>Hello " + esc(name) + ",</p>"
                + "<p>Thank you for subscribing to MyFuture CBC Revision. Your subscription has been recorded.</p>"
                + "<p>It should sync with the MyFuture Android app as well. If access does not update, open the app while connected to the internet and allow it to refresh.</p>"
                + button("Open your profile", PROFILE_URL)
                + button("Open MyFuture on Google Play", APP_URL));
    }

    private String weeklyPlanEmail(IbukaStudentAccount student, String name) {
        String subjects = getSubjects(student);
        return page("A simple plan for this week",
                "<p>Hello " + esc(name) + ",</p>"
                + "<p>Here is a simple plan for the coming week:</p>"
                + "<ol><li>Choose one topic and read the notes.</li><li>Complete a quiz on that topic.</li>"
                + "<li>Review incorrect answers and retry a few days later.</li></ol>"
                + (subjects.isEmpty() ? "" : "<p>Subjects available for your class include: <b>" + esc(subjects) + "</b>.</p>")
                + button("Choose your next activity", PROFILE_URL));
    }

    private String getClassLevelName(IbukaStudentAccount student) {
        if (student.getClasslevel() == null) return "";
        return curriLevelRepository.findById(student.getClasslevel())
                .map(CurriLevel::getName).orElse("");
    }

    private String getSubjects(IbukaStudentAccount student) {
        if (student.getClasslevel() == null) return "";
        List<Subject> subjects = subjectRepository.subjectsByClassLevel(student.getClasslevel());
        return subjects.stream().map(Subject::getName).filter(s -> s != null && !s.isBlank())
                .distinct().limit(8).collect(Collectors.joining(", "));
    }

    private String page(String heading, String body) {
        return "<!doctype html><html><head><meta charset='UTF-8'></head>"
                + "<body style='margin:0;padding:0;background:#f4f4f5;font-family:Arial,sans-serif;color:#27272a;'>"
                + "<div style='max-width:620px;margin:24px auto;background:#ffffff;border-radius:10px;overflow:hidden;'>"
                + "<div style='background:#800000;color:#ffffff;padding:22px;text-align:center;'>"
                + "<img src='" + FAVICON_URL + "' alt='MyFuture' width='32' height='32' style='vertical-align:middle;margin-right:8px;'>"
                + "<span style='font-size:22px;font-weight:bold;vertical-align:middle;'>" + BRAND + "</span>"
                + "<p style='margin:8px 0 0;color:#ffd7a3;'>Your partner in academic excellence</p></div>"
                + "<div style='padding:24px;line-height:1.6;'><h2 style='color:#800000;margin-top:0;'>"
                + esc(heading) + "</h2>" + body
                + "<p style='margin-top:28px;'>Happy learning,<br><b>The MyFuture Team</b><br>Ibuka Technologies</p>"
                + "</div><div style='background:#fff7ed;padding:14px 24px;color:#666;font-size:12px;'>"
                + "You are receiving this message to help you use MyFuture CBC Revision. "
                + "If these emails are not useful, contact MyFuture support to manage your communication preferences."
                + "</div></div></body></html>";
    }

    private String button(String label, String url) {
        return "<p style='margin:22px 0;'><a href='" + url + "' style='display:inline-block;background:#f28c28;"
                + "color:#ffffff;text-decoration:none;padding:12px 18px;border-radius:6px;font-weight:bold;'>"
                + esc(label) + "</a></p>";
    }

    private static Date daysAgo(Date from, int days) {
        return new Date(from.getTime() - days * 24L * 60 * 60 * 1000);
    }

    private static Date daysFrom(Date from, int days) {
        return new Date(from.getTime() + days * 24L * 60 * 60 * 1000);
    }

    private static Date mostRecent(Date... dates) {
        Date latest = null;
        for (Date date : dates) if (date != null && (latest == null || date.after(latest))) latest = date;
        return latest;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) if (value != null && !value.isBlank()) return value.trim();
        return "";
    }

    private static String safeSubjectName(String value) {
        return value == null || value.isBlank() ? "student" : value.replaceAll("[\\r\\n]", " ").trim();
    }

    private static String maskEmail(String email) {
        if (email == null) return "<null>";
        int at = email.indexOf('@');
        if (at <= 1) return "***" + (at >= 0 ? email.substring(at) : "");
        return email.substring(0, 1) + "***" + email.substring(at);
    }

    private static String esc(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
