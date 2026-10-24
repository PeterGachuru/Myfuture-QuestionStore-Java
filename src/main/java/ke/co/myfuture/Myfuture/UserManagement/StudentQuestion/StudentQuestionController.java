package ke.co.myfuture.Myfuture.UserManagement.StudentQuestion;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;

@RestController
@RequestMapping("student-questions")
public class StudentQuestionController {
    private final StudentQuestionService service;
    public StudentQuestionController(StudentQuestionService service) { this.service=service; }

    @PostMapping("create")
    public ResponseEntity<?> create(@RequestBody StudentQuestionRequest request) {
        try { return ResponseEntity.ok(success(service.create(request,authenticatedEmail()),"Question shared with nearby students")); }
        catch (SecurityException e) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(403,e.getMessage())); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(error(400,e.getMessage())); }
        catch (IllegalStateException e) { return ResponseEntity.status(HttpStatus.CONFLICT).body(error(409,e.getMessage())); }
    }
    @GetMapping("nearby")
    public ResponseEntity<?> nearby(@RequestParam Long studentId,@RequestParam Long installId,
                                    @RequestParam Long studentInId,@RequestParam Long parentId) {
        try { return ResponseEntity.ok(success(service.nearby(studentId,installId,studentInId,parentId,authenticatedEmail()),"Nearby questions loaded")); }
        catch (SecurityException e) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(403,e.getMessage())); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(error(400,e.getMessage())); }
    }
    private UniversalResponse success(Object entity,String message) {
        UniversalResponse r=new UniversalResponse(); r.setStatus("Success"); r.setMessage(message); r.setEntity(entity); r.setStatusCode(200); return r;
    }
    private Map<String,Object> error(int code,String message) { return Map.of("status","Error","statusCode",code,"message",message==null?"Request failed":message); }
    private String authenticatedEmail() {
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        if (auth==null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) return null;
        return auth.getName();
    }
}
