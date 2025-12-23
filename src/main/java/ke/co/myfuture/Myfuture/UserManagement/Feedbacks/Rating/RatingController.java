package ke.co.myfuture.Myfuture.UserManagement.Feedbacks.Rating;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("student/rating")
public class RatingController {
    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    IbukaStudentAccountRepository ibukaStudentAccountRepository;

    @PostMapping("add")
    public ResponseEntity<?> newStudentAccount(@RequestBody Rating rating) {
        System.out.println(rating);
        if (rating.id != null) return null;

        if (rating.getStudentId() != null){
            Optional<IbukaStudentAccount> ibukaStudentAccount = ibukaStudentAccountRepository.findById(rating.getStudentId());
            ibukaStudentAccount.ifPresent(rating::setIbukaStudentAccount);
        }

        Rating savedRating = ratingRepository.save(rating);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedRating);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("recent")
    public ResponseEntity<?> recentratings() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Retrieved successfully");
        response.setEntity(ratingRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
