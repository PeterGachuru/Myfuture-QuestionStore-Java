package ke.co.myfuture.Myfuture.UserManagement.Feedbacks.Rating;

import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/ratings")
@AllArgsConstructor
public class AdminRatingController {

    private final RatingRepository ratingRepository;
    private final CurriLevelService curriLevelService;


    @GetMapping
    public String list(Model model) {

        List<Rating> ratings =
                ratingRepository.findByDeletedAtIsNullOrderByIdDesc(PageRequest.of(0,300));

        model.addAttribute("ratings", ratings);

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