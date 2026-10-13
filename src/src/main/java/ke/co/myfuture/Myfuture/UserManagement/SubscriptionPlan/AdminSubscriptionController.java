package ke.co.myfuture.Myfuture.UserManagement.SubscriptionPlan;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/subscriptions")
@AllArgsConstructor
public class AdminSubscriptionController {

    private final SubscriptionPlanService subscriptionService;
    private final SubscriptionPlanRepository subscriptionPlanRepository;


    @GetMapping
    public String subscriptions(Model model) {

        model.addAttribute("plans", subscriptionService.getAllPlans());

        model.addAttribute("plan", new SubscriptionPlan());

        return "admin/subscriptions";
    }

    @PostMapping("/save")
    public String savePlan(@ModelAttribute SubscriptionPlan plan) {
        System.out.println("==============Save===============");
//        System.out.println(plan);

        subscriptionService.save(plan);

        return "redirect:/admin/subscriptions";
    }

    @GetMapping("/edit/{id}")
    public String editPlan(@PathVariable Long id, Model model) {

        SubscriptionPlan plan = subscriptionPlanRepository.findById(id).orElseThrow();

        model.addAttribute("plans", subscriptionPlanRepository.findAll());
        model.addAttribute("plan", plan);

        return "admin/subscriptions";
    }

    @GetMapping("/toggle/{id}")
    public String togglePlan(@PathVariable Long id) {

        subscriptionService.togglePlan(id);

        return "redirect:/admin/subscriptions";
    }
}