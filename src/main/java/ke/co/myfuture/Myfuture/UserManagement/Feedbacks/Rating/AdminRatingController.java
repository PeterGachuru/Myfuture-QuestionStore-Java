package ke.co.myfuture.Myfuture.UserManagement.Feedbacks.Rating;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelService;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/admin/ratings")
@AllArgsConstructor
public class AdminRatingController {

    private final RatingRepository ratingRepository;
    private final CurriLevelService curriLevelService;
    private final UserRepository userRepository;


    @GetMapping
    public String list(Model model) {

        List<Rating> ratings = ratingRepository.findByDeletedAtIsNullOrderByIdDesc(PageRequest.of(0,300));

        Set<Long> parentIds = new HashSet<>();
        for (Rating rating : ratings) {
            IbukaStudentAccount student = rating.getIbukaStudentAccount();
            if (student != null && student.getParent() != null) parentIds.add(student.getParent());
        }
        Map<Long, String> phoneByParentId = new HashMap<>();
        if (!parentIds.isEmpty()) {
            for (User user : userRepository.findAllById(parentIds)) {
                phoneByParentId.put(user.getId(), user.getPhoneNumber());
            }
        }
        Map<Long, String> phoneNumbers = new HashMap<>();
        for (Rating rating : ratings) {
            IbukaStudentAccount student = rating.getIbukaStudentAccount();
            if (student != null) phoneNumbers.put(rating.getId(), phoneByParentId.get(student.getParent()));
        }

        model.addAttribute("ratings", ratings);
        model.addAttribute("phoneNumbers", phoneNumbers);

        return "admin/ratings";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {

        Rating rating = ratingRepository.findById(id).orElseThrow();

        rating.delete();

        ratingRepository.save(rating);

        return "redirect:/admin/ratings";
    }

}
