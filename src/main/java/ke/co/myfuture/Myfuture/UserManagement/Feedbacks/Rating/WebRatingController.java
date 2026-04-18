package ke.co.myfuture.Myfuture.UserManagement.Feedbacks.Rating;


import ke.co.myfuture.Myfuture.HttpAuth.CookieService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/read")
@AllArgsConstructor
public class WebRatingController {
    private final RatingRepository ratingRepository;
    private final CookieService cookieService;
    @PostMapping("/notes/rate")
    @ResponseBody
    public ResponseEntity<?> rateNotes(@RequestBody Rating rating,
                                       HttpServletRequest request) {

        // Set required fields
        rating.setSource("WEB_NOTES");
        rating.setCreatedBy(cookieService.getVisitorId(request));
        rating.setUpdatedBy(rating.getCreatedBy());

        ratingRepository.save(rating);

        return ResponseEntity.ok().build();
    }
}
