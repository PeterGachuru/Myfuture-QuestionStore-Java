package ke.co.myfuture.Myfuture.UserManagement.StudentNotesProgress;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
public class StudentNotesCompletionService {
    private static final ZoneId APP_ZONE = ZoneId.of("Africa/Nairobi");

    private final StudentNotesCompletionRepository completionRepository;
    private final IbukaStudentAccountRepository studentRepository;

    public StudentNotesCompletionService(
            StudentNotesCompletionRepository completionRepository,
            IbukaStudentAccountRepository studentRepository
    ) {
        this.completionRepository = completionRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(transactionManager = "studentManagementTransactionManager")
    public StudentNotesCompletion complete(StudentNotesCompletionRequest request) {
        validate(request);
        IbukaStudentAccount student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student was not found"));
        Optional<StudentNotesCompletion> existing = completionRepository
                .findByStudent_IdAndNotesIdAndNotesUpdatedAt(
                        student.getId(), request.getNotesId(), request.getNotesUpdatedAt().trim()
                );
        StudentNotesCompletion completion = existing.orElseGet(StudentNotesCompletion::new);
        completion.setStudent(student);
        completion.setNotesId(request.getNotesId());
        completion.setSubtopicId(request.getSubtopicId());
        completion.setNotesUpdatedAt(request.getNotesUpdatedAt().trim());
        completion.setCompletedAt(parseCompletedAt(request.getCompletedAt()));
        return completionRepository.save(completion);
    }

    @Transactional(readOnly = true, transactionManager = "studentManagementTransactionManager")
    public boolean isComplete(Long studentId, Long notesId, String notesUpdatedAt) {
        if (studentId == null || notesId == null || notesUpdatedAt == null || notesUpdatedAt.isBlank()) {
            return false;
        }
        return completionRepository.findByStudent_IdAndNotesIdAndNotesUpdatedAt(
                studentId, notesId, notesUpdatedAt.trim()
        ).isPresent();
    }

    private void validate(StudentNotesCompletionRequest request) {
        if (request == null || request.getStudentId() == null || request.getStudentId() <= 0
                || request.getNotesId() == null || request.getNotesId() <= 0
                || request.getSubtopicId() == null || request.getSubtopicId() <= 0
                || request.getNotesUpdatedAt() == null || request.getNotesUpdatedAt().isBlank()) {
            throw new IllegalArgumentException("Valid student, notes, subtopic, and notes version are required");
        }
        if (request.getNotesUpdatedAt().length() > 80) {
            throw new IllegalArgumentException("Notes version is too long");
        }
    }

    private LocalDateTime parseCompletedAt(String value) {
        if (value == null || value.isBlank()) return LocalDateTime.now(APP_ZONE);
        try {
            return LocalDateTime.ofInstant(Instant.parse(value), APP_ZONE);
        } catch (DateTimeParseException ignored) {
            try {
                return OffsetDateTime.parse(value).atZoneSameInstant(APP_ZONE).toLocalDateTime();
            } catch (DateTimeParseException invalid) {
                throw new IllegalArgumentException("completedAt must be an ISO-8601 timestamp");
            }
        }
    }
}
