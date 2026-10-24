package ke.co.myfuture.Myfuture.UserManagement.AdminDashboard;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.Install.Install2Repository;
import ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails.ScheduledEmailsRepo;
import ke.co.myfuture.Myfuture.UserManagement.DashboardAnalytics.DashboardAggregationCheckpoint;
import ke.co.myfuture.Myfuture.UserManagement.DashboardAnalytics.DashboardAggregationCheckpointRepository;
import ke.co.myfuture.Myfuture.UserManagement.DashboardAnalytics.DashboardMetricAggregate;
import ke.co.myfuture.Myfuture.UserManagement.DashboardAnalytics.DashboardMetricAggregateRepository;
import ke.co.myfuture.Myfuture.UserManagement.Chatmessage.ChatmessageRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee.ContestInviteeRepository;
import ke.co.myfuture.Myfuture.UserManagement.Contest.ContestRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.UserManagement.PageVisit.PageVisitRepository;
import ke.co.myfuture.Myfuture.UserManagement.Post.PostRepository;
import ke.co.myfuture.Myfuture.UserManagement.Post.Postatempt.PostattemptRepository;
import ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizDoneRepository;
import ke.co.myfuture.Myfuture.UserManagement.Referral.ReferralRepository;
import ke.co.myfuture.Myfuture.UserManagement.StudySubscription.StudySubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardAnalyticsService {
    private static final ZoneId REPORT_ZONE = ZoneId.of("Africa/Nairobi");
    private static final LocalDateTime HISTORY_FLOOR = LocalDateTime.of(2022, 1, 1, 0, 0);
    private static final int BACKFILL_CHUNK_DAYS = 1;
    private static final int RECENT_REFRESH_DAYS = 7;
    private static final List<GraphDefinition> GRAPHS = List.of(
            new GraphDefinition("students", "studentsChart", "Student Registrations", "Students"),
            new GraphDefinition("accounts", "accountsCreatedPerDay", "Accounts Created", "Accounts"),
            new GraphDefinition("referrals", "referralsChart", "Referrals", "Referrals"),
            new GraphDefinition("quizzes", "quizChart", "Quizzes Done", "Quizzes"),
            new GraphDefinition("active_students", "activeStudentsChart", "Active Students (DAU)", "Active Users"),
            new GraphDefinition("contest_attempts", "contestAttemptsChart", "Contest Attempts", "Contest Attempts"),
            new GraphDefinition("post_attempts", "postAttemptsChart", "Post Attempts", "Post Attempts"),
            new GraphDefinition("transactions", "transactionsChart", "Transactions", "Transactions"),
            new GraphDefinition("revenue", "revenueChart", "Revenue", "Amount (KES)"),
            new GraphDefinition("visits", "visitsChart", "Page Visits", "Visits"),
            new GraphDefinition("chat_messages", "chatChart", "Chat Messages Created", "Messages"),
            new GraphDefinition("posts", "postsChart", "Posts Created", "Posts"),
            new GraphDefinition("contests", "contestChart", "Contests Created", "Contests"),
            new GraphDefinition("installs", "installsChart", "Total Installs", "Installs"),
            new GraphDefinition("installs_with_accounts", "installsWithAccountsChart", "Installs (With Account)", "Users"),
            new GraphDefinition("account_conversion", "conversionChart", "Account Conversion (%)", "Percentage (%)"),
            new GraphDefinition("emails_scheduled", "emailsScheduledChart", "Emails Scheduled", "Emails"),
            new GraphDefinition("emails_sent", "emailsSentChart", "Emails Sent", "Emails")
    );

    private final GraphService graphService;
    private final DashboardAggregateWriter aggregateWriter;
    private final DashboardMetricAggregateRepository aggregateRepository;
    private final DashboardAggregationCheckpointRepository checkpointRepository;
    private final IbukaStudentAccountRepository students;
    private final UserRepository users;
    private final ReferralRepository referrals;
    private final QuizDoneRepository quizzes;
    private final ContestInviteeRepository contestAttempts;
    private final PostattemptRepository postAttempts;
    private final StudySubscriptionRepository subscriptions;
    private final PageVisitRepository visits;
    private final ChatmessageRepository chatMessages;
    private final PostRepository posts;
    private final ContestRepository contests;
    private final Install2Repository installs;
    private final ScheduledEmailsRepo emails;

    @Value("${admin.dashboard.analytics.enabled:true}")
    private boolean enabled;

    @Value("${admin.dashboard.analytics.backfill-days-per-run:1}")
    private int backfillDaysPerRun;

    private volatile boolean historicalBackfillComplete;
    private LocalDateTime lastRecentRefreshAt;

    public List<GraphData> buildGraphs(String resolution, LocalDateTime requestedFrom, LocalDateTime requestedTo) {
        boolean hourly = "HOUR".equals(resolution);
        LocalDateTime from = floor(requestedFrom, hourly);
        LocalDateTime to = floor(requestedTo, hourly).plusHours(hourly ? 1 : 0).plusDays(hourly ? 0 : 1);
        if (!to.isAfter(from)) to = from.plusHours(hourly ? 1 : 24);

        Date fromDate = asDate(from);
        Date toDate = asDate(to);
        LocalDateTime liveTailStart = hourly
                ? floor(LocalDateTime.now(REPORT_ZONE).minusHours(2), true)
                : floor(LocalDateTime.now(REPORT_ZONE).minusDays(2), false);
        LocalDateTime liveTo = to.isBefore(LocalDateTime.now(REPORT_ZONE))
                ? to : LocalDateTime.now(REPORT_ZONE);
        boolean shortFallbackRange = Duration.between(from, to)
                .compareTo(hourly ? Duration.ofDays(31) : Duration.ofDays(90)) <= 0;

        List<GraphData> result = new ArrayList<>(GRAPHS.size());
        for (GraphDefinition definition : GRAPHS) {
            Map<LocalDateTime, BigDecimal> values = new HashMap<>();
            aggregateRepository.findByMetricKeyAndResolutionAndBucketStartGreaterThanEqualAndBucketStartLessThanOrderByBucketStart(
                            definition.metricKey(), resolution, fromDate, toDate)
                    .forEach(row -> values.put(asLocal(row.getBucketStart()), row.getMetricValue()));

            boolean historyMissing = checkpointRepository.findByMetricKeyAndResolution(definition.metricKey(), resolution)
                    .map(checkpoint -> !checkpoint.isComplete() && from.isBefore(asLocal(checkpoint.getNextBefore())))
                    .orElse(true);
            LocalDateTime liveFrom = historyMissing && shortFallbackRange
                    ? from : (from.isAfter(liveTailStart) ? from : liveTailStart);
            boolean includeLiveTail = liveFrom.isBefore(to) && liveFrom.isBefore(liveTo);
            Date liveFromDate = asDate(liveFrom);
            Date liveToDate = asDate(liveTo);
            if (includeLiveTail && liveToDate.after(liveFromDate)) {
                values.keySet().removeIf(bucket -> !bucket.isBefore(liveFrom));
                try {
                    readPoints(definition.metricKey(), liveFromDate, liveToDate, hourly)
                            .forEach((bucket, value) -> values.put(asLocal(bucket), value));
                } catch (Exception ex) {
                    log.warn("Live dashboard tail unavailable for metric={}", definition.metricKey(), ex);
                }
            }

            List<String> labels = new ArrayList<>();
            List<BigDecimal> series = new ArrayList<>();
            DateTimeFormatter labelFormat = DateTimeFormatter.ofPattern(hourly ? "yyyy-MM-dd HH:00" : "yyyy-MM-dd");
            LocalDateTime bucket = from;
            while (bucket.isBefore(to)) {
                labels.add(bucket.format(labelFormat));
                series.add(values.getOrDefault(bucket, BigDecimal.ZERO));
                bucket = hourly ? bucket.plusHours(1) : bucket.plusDays(1);
            }
            String title = "active_students".equals(definition.metricKey()) && hourly
                    ? "Active Students (unique per hour)" : definition.title();
            result.add(graphService.buildTimeSeries(definition.id(), title, labels, series,
                    hourly ? "Hour (Africa/Nairobi)" : "Date (Africa/Nairobi)", definition.yLabel()));
        }
        return result;
    }


    @Scheduled(initialDelayString = "${admin.dashboard.analytics.backfill-initial-delay-ms:10000}",
            fixedDelayString = "${admin.dashboard.analytics.backfill-delay-ms:5000}")
    public synchronized void backfillDashboardHistory() {
        if (!enabled || historicalBackfillComplete) return;
        List<DashboardAggregationCheckpoint> checkpoints = checkpointRepository.findAll();
        if (checkpoints.size() >= GRAPHS.size() * 2
                && checkpoints.stream().allMatch(DashboardAggregationCheckpoint::isComplete)) {
            historicalBackfillComplete = true;
            log.info("Dashboard historical backfill already complete; background source queries are disabled");
            return;
        }
        LocalDateTime now = LocalDateTime.now(REPORT_ZONE);
        long startedAtNanos = System.nanoTime();
        log.info("Dashboard historical backfill cycle started backfillDaysPerRun={} at={}",
                backfillDaysPerRun, now);
        try {
            backfillHistoricalDaysBreadthFirst(now, true);
            backfillHistoricalDaysBreadthFirst(now, false);
        } finally {
            List<DashboardAggregationCheckpoint> updatedCheckpoints = checkpointRepository.findAll();
            if (updatedCheckpoints.size() >= GRAPHS.size() * 2
                    && updatedCheckpoints.stream().allMatch(DashboardAggregationCheckpoint::isComplete)) {
                historicalBackfillComplete = true;
                log.info("Dashboard historical backfill completed for all metrics and resolutions");
            }
            long elapsedMs = (System.nanoTime() - startedAtNanos) / 1_000_000;
            log.info("Dashboard historical backfill cycle finished elapsedMs={}", elapsedMs);
        }
    }

    public String historyStatus(LocalDateTime requestedFrom) {
        List<DashboardAggregationCheckpoint> checkpoints = checkpointRepository.findAll();
        if (checkpoints.size() < GRAPHS.size() * 2) {
            return "Historical summaries are initializing; older ranges may be incomplete while the background backfill starts.";
        }
        if (checkpoints.stream().allMatch(DashboardAggregationCheckpoint::isComplete)) {
            return "Historical hourly and daily summaries are fully prepared.";
        }
        LocalDateTime earliestCommonCoverage = checkpoints.stream()
                .map(checkpoint -> asLocal(checkpoint.getNextBefore()))
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now(REPORT_ZONE));
        if (requestedFrom.isBefore(earliestCommonCoverage)) {
            return "Backfill is in progress. All graphs are complete from " + earliestCommonCoverage
                    + " onward; earlier selected buckets may still be filling in.";
        }
        return "Selected period is covered by saved summaries. Older historical buckets are still being prepared.";
    }

    public synchronized void refreshRecentDataOnDashboardVisit(LocalDateTime requestedTo) {
        if (!enabled) return;
        LocalDateTime now = LocalDateTime.now(REPORT_ZONE);
        if (requestedTo.isBefore(now.minusDays(RECENT_REFRESH_DAYS))) return;
        if (lastRecentRefreshAt != null
                && Duration.between(lastRecentRefreshAt, now).compareTo(Duration.ofMinutes(1)) < 0) {
            return;
        }
        long startedAtNanos = System.nanoTime();
        log.info("Dashboard recent data refresh started due to dashboard visit at={}", now);
        refreshRecent(now, true);
        refreshRecent(now, false);
        lastRecentRefreshAt = now;
        log.info("Dashboard recent data refresh finished due to dashboard visit elapsedMs={}",
                (System.nanoTime() - startedAtNanos) / 1_000_000);
    }
    private void refreshRecent(LocalDateTime now, boolean hourly) {
        String resolution = hourly ? "HOUR" : "DAY";
        LocalDateTime from = floor(now.minusDays(RECENT_REFRESH_DAYS), hourly);
        LocalDateTime liveBoundary = hourly ? floor(now.minusHours(2), true) : floor(now.minusDays(2), false);
        LocalDateTime to = liveBoundary.isBefore(now) ? liveBoundary : floor(now, hourly);
        if (!to.isAfter(from)) return;
        refreshRange(resolution, hourly, from, to);
    }

    private void backfillHistoricalDaysBreadthFirst(LocalDateTime now, boolean hourly) {
        String resolution = hourly ? "HOUR" : "DAY";
        LocalDateTime initialCursor = hourly
                ? floor(now.minusHours(2), true)
                : floor(now.minusDays(2), false);
        Date initialCursorDate = asDate(initialCursor);
        List<MetricCheckpoint> metrics = new ArrayList<>(GRAPHS.size());
        for (GraphDefinition definition : GRAPHS) {
            try {
                DashboardAggregationCheckpoint checkpoint = aggregateWriter.getOrCreateCheckpoint(
                        definition.metricKey(), resolution, initialCursorDate);
                metrics.add(new MetricCheckpoint(definition, checkpoint));
            } catch (Exception ex) {
                log.error("Dashboard checkpoint initialization failed for metric={} resolution={}",
                        definition.metricKey(), resolution, ex);
            }
        }

        int daysPerRun = Math.max(1, backfillDaysPerRun);
        for (int day = 0; day < daysPerRun; day++) {
            for (MetricCheckpoint metric : metrics) {
                DashboardAggregationCheckpoint checkpoint = metric.checkpoint();
                if (!checkpoint.isComplete() && !asLocal(checkpoint.getNextBefore()).isAfter(HISTORY_FLOOR)) {
                    aggregateWriter.updateCheckpoint(checkpoint, asDate(HISTORY_FLOOR), true);
                }
            }

            LocalDateTime newestUnfinishedCursor = metrics.stream()
                    .map(MetricCheckpoint::checkpoint)
                    .filter(checkpoint -> !checkpoint.isComplete())
                    .map(checkpoint -> asLocal(checkpoint.getNextBefore()))
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            if (newestUnfinishedCursor == null) return;

            LocalDateTime from = newestUnfinishedCursor.minusDays(BACKFILL_CHUNK_DAYS);
            if (from.isBefore(HISTORY_FLOOR)) from = HISTORY_FLOOR;
            from = floor(from, hourly);
            boolean failed = false;
            for (MetricCheckpoint metric : metrics) {
                DashboardAggregationCheckpoint checkpoint = metric.checkpoint();
                if (checkpoint.isComplete()
                        || !asLocal(checkpoint.getNextBefore()).equals(newestUnfinishedCursor)) {
                    continue;
                }
                if (!refreshMetricRange(metric.definition(), resolution, hourly, from, newestUnfinishedCursor)) {
                    failed = true;
                    continue;
                }
                aggregateWriter.updateCheckpoint(checkpoint, asDate(from), !from.isAfter(HISTORY_FLOOR));
            }
            if (failed) {
                log.warn("Dashboard breadth-first backfill paused at {} resolution={} because one or more metric queries failed",
                        newestUnfinishedCursor, resolution);
                return;
            }
        }
    }
    private void refreshRange(String resolution, boolean hourly, LocalDateTime from, LocalDateTime to) {
        for (GraphDefinition definition : GRAPHS) {
            refreshMetricRange(definition, resolution, hourly, from, to);
        }
    }

    private boolean refreshMetricRange(GraphDefinition definition, String resolution, boolean hourly,
                                       LocalDateTime from, LocalDateTime to) {
        Date fromDate = asDate(from);
        Date toDate = asDate(to);
        try {
            Map<Date, BigDecimal> points = readPoints(definition.metricKey(), fromDate, toDate, hourly);
            aggregateWriter.replaceRange(definition.metricKey(), resolution, fromDate, toDate, points);
            log.info("Completed dashboard aggregation metric={} resolution={} from={} toExclusive={} nonZeroBuckets={}",
                    definition.metricKey(), resolution, from, to, points.size());
            return true;
        } catch (Exception ex) {
            log.error("Dashboard aggregate refresh failed for metric={} resolution={} from={} to={}",
                    definition.metricKey(), resolution, from, to, ex);
            return false;
        }
    }

    private Map<Date, BigDecimal> readPoints(String metricKey, Date from, Date to, boolean hourly) {
        List<Object[]> rows = switch (metricKey) {
            case "students" -> students.dashboardRegistrations(from, to, hourly);
            case "accounts" -> users.dashboardCreatedAccounts(from, to, hourly);
            case "referrals" -> referrals.dashboardReferrals(from, to, hourly);
            case "quizzes" -> quizzes.dashboardQuizzes(from, to, hourly);
            case "active_students" -> quizzes.dashboardActiveStudents(from, to, hourly);
            case "contest_attempts" -> contestAttempts.dashboardContestAttempts(from, to, hourly);
            case "post_attempts" -> postAttempts.dashboardPostAttempts(from, to, hourly);
            case "transactions" -> subscriptions.dashboardTransactions(from, to, hourly);
            case "revenue" -> subscriptions.dashboardRevenue(from, to, hourly);
            case "visits" -> visits.dashboardVisits(from, to, hourly);
            case "chat_messages" -> chatMessages.dashboardMessages(from, to, hourly);
            case "posts" -> posts.dashboardPosts(from, to, hourly);
            case "contests" -> contests.dashboardContests(from, to, hourly);
            case "installs" -> installs.dashboardInstalls(from, to, hourly);
            case "installs_with_accounts" -> installs.dashboardInstallsWithAccounts(from, to, hourly);
            case "account_conversion" -> installs.dashboardAccountConversion(from, to, hourly);
            case "emails_scheduled" -> emails.dashboardEmailsScheduled(from, to, hourly);
            case "emails_sent" -> emails.dashboardEmailsSent(from, to, hourly);
            default -> throw new IllegalArgumentException("Unknown dashboard metric: " + metricKey);
        };
        Map<Date, BigDecimal> values = new HashMap<>();
        for (Object[] row : rows) {
            Date bucket = asDate(parseBucket(row[0].toString()));
            Object value = row[1];
            BigDecimal amount = value instanceof BigDecimal decimal
                    ? decimal
                    : BigDecimal.valueOf(((Number) value).doubleValue());
            values.put(bucket, amount);
        }
        return values;
    }

    private static LocalDateTime parseBucket(String value) {
        if (value.length() <= 10) return LocalDate.parse(value).atStartOfDay();
        return LocalDateTime.parse(value.replace(' ', 'T'));
    }

    private static LocalDateTime floor(LocalDateTime value, boolean hourly) {
        return hourly ? value.withMinute(0).withSecond(0).withNano(0) : value.toLocalDate().atStartOfDay();
    }

    private static Date asDate(LocalDateTime value) {
        return Date.from(value.atZone(REPORT_ZONE).toInstant());
    }

    private static LocalDateTime asLocal(Date value) {
        return LocalDateTime.ofInstant(value.toInstant(), REPORT_ZONE);
    }

    private record GraphDefinition(String metricKey, String id, String title, String yLabel) {}
    private record MetricCheckpoint(GraphDefinition definition, DashboardAggregationCheckpoint checkpoint) {}
}
