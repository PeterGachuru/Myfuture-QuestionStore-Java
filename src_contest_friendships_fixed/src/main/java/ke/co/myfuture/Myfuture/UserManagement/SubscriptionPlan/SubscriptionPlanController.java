package ke.co.myfuture.Myfuture.UserManagement.SubscriptionPlan;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptionsplans")
@AllArgsConstructor
public class SubscriptionPlanController {
    private final SubscriptionPlanService service;

    @GetMapping
    public List<SubscriptionPlan> getPlans() {
        return service.getActivePlans();
    }

    @PostMapping
    public SubscriptionPlan createPlan(@RequestBody SubscriptionPlan plan) {
        return service.createPlan(plan);
    }

    @PutMapping("/{id}")
    public SubscriptionPlan updatePlan(@PathVariable Long id,
                                       @RequestBody SubscriptionPlan plan) {
        return service.updatePlan(id, plan);
    }
}
