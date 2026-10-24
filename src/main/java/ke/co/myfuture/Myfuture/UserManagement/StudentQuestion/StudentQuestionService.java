package ke.co.myfuture.Myfuture.UserManagement.StudentQuestion;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevel;
import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.UserManagement.Cgroup.Cgroup;
import ke.co.myfuture.Myfuture.UserManagement.Cgroup.CgroupRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StudentQuestionService {
    private final StudentQuestionRepository questions;
    private final IbukaStudentAccountRepository students;
    private final CurriLevelRepository levels;
    private final CgroupRepository groups;
    private final IbukaStudentAccountService studentService;

    public StudentQuestionService(StudentQuestionRepository questions, IbukaStudentAccountRepository students,
            CurriLevelRepository levels, CgroupRepository groups, IbukaStudentAccountService studentService) {
        this.questions=questions; this.students=students; this.levels=levels; this.groups=groups; this.studentService=studentService;
    }

    @Transactional(transactionManager = "studentManagementTransactionManager")
    public StudentQuestionDTO create(StudentQuestionRequest r, String authenticatedEmail) {
        if (r == null || r.getClientQuestionId()==null || r.getClientQuestionId().isBlank() || r.getClientQuestionId().length()>80)
            throw new IllegalArgumentException("A question request ID is required");
        if (r.getQuestion()==null || r.getQuestion().isBlank() || r.getQuestion().trim().length()>1200)
            throw new IllegalArgumentException("Write a question of up to 1,200 characters");
        if (r.getStudentId()==null || r.getInstallId()==null || r.getStudentInId()==null)
            throw new IllegalArgumentException("Student and installation details are required");
        var duplicate=questions.findByClientQuestionId(r.getClientQuestionId());
        if (duplicate.isPresent()) {
            StudentQuestion q=duplicate.get();
            if (!Objects.equals(q.getStudentaccount().getId(),r.getStudentId()) || !Objects.equals(q.getInstallId(),r.getInstallId()) || !Objects.equals(q.getStudentInId(),r.getStudentInId()))
                throw new SecurityException("This request ID belongs to a different student");
            return toDto(q);
        }
        IbukaStudentAccount student=students.findById(r.getStudentId()).orElseThrow(()->new IllegalArgumentException("Student profile was not found"));
        verify(student,r.getInstallId(),r.getStudentInId(),r.getParentId(),authenticatedEmail);
        if (student.getClasslevel()==null || levels.findById(student.getClasslevel()).isEmpty())
            throw new IllegalArgumentException("A valid class level is required to share a question");
        if (student.getSender()==null) student=studentService.createSenderIdIfMissing(student);
        if (student.getSender()==null) throw new IllegalStateException("Student chat profile is not ready yet");
        Cgroup group=new Cgroup(); group.setName("Student question"); group.setDescription("Discussion for a question shared by a student"); group.setType("group");
        StudentQuestion q=new StudentQuestion(); q.setClientQuestionId(r.getClientQuestionId()); q.setQuestion(r.getQuestion().trim());
        q.setStudentaccount(student); q.setCgroup(groups.save(group)); q.setInstallId(r.getInstallId()); q.setStudentInId(r.getStudentInId());
        return toDto(questions.save(q));
    }

    @Transactional(readOnly=true, transactionManager = "studentManagementTransactionManager")
    public List<StudentQuestionDTO> nearby(Long id, Long installId, Long studentInId,
                                           Long parentId, String authenticatedEmail) {
        IbukaStudentAccount viewer=students.findById(id).orElseThrow(()->new IllegalArgumentException("Student profile was not found"));
        verify(viewer,installId,studentInId,parentId,authenticatedEmail);
        if (viewer.getClasslevel()==null) return List.of();
        // Existing catalog lookup returns this grade and adjacent numbering in the same curriculum.
        List<Long> closeLevels=levels.classesAround(viewer.getClasslevel());
        if (closeLevels==null || closeLevels.isEmpty()) return List.of();
        return questions.findNearby(closeLevels,PageRequest.of(0,100)).stream().map(this::toDto).collect(Collectors.toList());
    }

    private void verify(IbukaStudentAccount student,Long installId,Long studentInId,
                        Long parentId,String authenticatedEmail) {
        boolean installationOwns = Objects.equals(student.getInstallId(),installId)
                && Objects.equals(student.getInid(),studentInId);
        boolean authenticatedParentOwns = authenticatedEmail != null && !authenticatedEmail.isBlank()
                && student.getParentUsername() != null
                && student.getParentUsername().equalsIgnoreCase(authenticatedEmail)
                && Objects.equals(student.getParent(),parentId);
        if (!installationOwns && !authenticatedParentOwns)
            throw new SecurityException("This installation is not linked to the selected student");
    }
    private StudentQuestionDTO toDto(StudentQuestion q) {
        CurriLevel level=levels.findById(q.getStudentaccount().getClasslevel()).orElse(null);
        return StudentQuestionDTO.from(q,level==null?"Student":level.getName(),level==null?null:level.getNumbering());
    }
}
