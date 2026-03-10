package ke.co.myfuture.Myfuture.UserManagement.Feedbacks.Rating;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/ratings")
public class AdminRatingController {

    private final RatingRepository ratingRepository;

    public AdminRatingController(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

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