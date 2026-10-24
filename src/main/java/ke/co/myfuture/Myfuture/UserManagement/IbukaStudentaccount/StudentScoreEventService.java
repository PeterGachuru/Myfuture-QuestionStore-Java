package ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount;

import ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.Cronjobs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StudentScoreEventService {
    private static final long MAX_SCORE_DELTA = 10_000L;

    private final StudentScoreEventRepository scoreEventRepository;
    private final IbukaStudentAccountRepository studentRepository;
    private final Cronjobs cronjobs;

    public StudentScoreEventService(
            StudentScoreEventRepository scoreEventRepository,
            IbukaStudentAccountRepository studentRepository,
            Cronjobs cronjobs
    ) {
        this.scoreEventRepository = scoreEventRepository;
        this.studentRepository = studentRepository;
        this.cronjobs = cronjobs;
    }

    @Transactional(transactionManager = "studentManagementTransactionManager")
    public Result record(StudentScoreEventRequest request, String authenticatedEmail) {
        if (request == null || request.getEventId() == null || request.getEventId().isBlank()
                || request.getEventId().length() > 160) {
            throw new IllegalArgumentException("A valid score event ID is required");
        }
        if (request.getStudentId() == null || request.getStudentId() < 1) {
            throw new IllegalArgumentException("A valid student ID is required");
        }
        Long delta = request.getScoreDelta();
        if (delta == null || delta < 1 || delta > MAX_SCORE_DELTA) {
            throw new IllegalArgumentException("Score delta must be between 1 and " + MAX_SCORE_DELTA);
        }

        IbukaStudentAccount student = studentRepository.findByIdForScoreUpdate(request.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException(request.getStudentId()));

        boolean installationOwnsStudent = request.getInstallId() != null
                && request.getStudentInId() != null
                && request.getInstallId().equals(student.getInstallId())
                && request.getStudentInId().equals(student.getInid());
        boolean accountOwnsStudent = authenticatedEmail != null && student.getParentUsername() != null
                && authenticatedEmail.equalsIgnoreCase(student.getParentUsername());
        if (!installationOwnsStudent && !accountOwnsStudent) {
            throw new SecurityException("The caller does not own this student account");
        }

        return recordForStudent(request.getEventId(), student, delta, request.getSource(),
                request.getInstallId(), request.getOccurredAt());
    }

    /** Records a score generated and verified inside the backend, without client ownership checks. */
    @Transactional(transactionManager = "studentManagementTransactionManager")
    public Result recordTrusted(String eventId, Long studentId, Long scoreDelta, String source,
                                Long installId, java.time.Instant occurredAt) {
        if (eventId == null || eventId.isBlank() || eventId.length() > 160
                || studentId == null || studentId < 1
                || scoreDelta == null || scoreDelta < 1 || scoreDelta > MAX_SCORE_DELTA) {
            throw new IllegalArgumentException("Invalid trusted score event");
        }
        IbukaStudentAccount student = studentRepository.findByIdForScoreUpdate(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return recordForStudent(eventId, student, scoreDelta, source, installId, occurredAt);
    }

    private Result recordForStudent(String eventId, IbukaStudentAccount student, Long delta,
                                    String source, Long installId, java.time.Instant occurredAt) {
        Optional<StudentScoreEvent> existing = scoreEventRepository.findById(eventId);
        if (existing.isPresent()) {
            if (!existing.get().getStudent().getId().equals(student.getId())) {
                throw new IllegalArgumentException("Score event ID is already assigned to another student");
            }
            return new Result(false, student.getTotalScore() == null ? 0L : student.getTotalScore());
        }

        StudentScoreEvent event = new StudentScoreEvent();
        event.setEventId(eventId);
        event.setStudent(student);
        event.setScoreDelta(delta);
        event.setSource(safeSource(source));
        event.setInstallId(installId);
        event.setOccurredAt(occurredAt);
        scoreEventRepository.saveAndFlush(event);

        long updatedTotal = Math.addExact(student.getTotalScore() == null ? 0L : student.getTotalScore(), delta);
        student.setTotalScore(updatedTotal);
        studentRepository.saveAndFlush(student);
        try {
            cronjobs.analyzeScoresDeep();
        } catch (Exception exception) {
            // The event and total score are authoritative; analytics can be recomputed later.
        }
        return new Result(true, updatedTotal);
    }

    private static String safeSource(String source) {
        if (source == null || source.isBlank()) return "unspecified";
        String cleaned = source.trim().replaceAll("[\\r\\n]", " ");
        return cleaned.length() > 120 ? cleaned.substring(0, 120) : cleaned;
    }

    public record Result(boolean applied, long totalScore) {}

    public static final class StudentNotFoundException extends RuntimeException {
        public StudentNotFoundException(Long studentId) {
            super("Student not found: " + studentId);
        }
    }
}
