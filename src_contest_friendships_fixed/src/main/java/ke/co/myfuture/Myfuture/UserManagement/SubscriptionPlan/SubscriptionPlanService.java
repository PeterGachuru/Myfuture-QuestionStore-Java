package ke.co.myfuture.Myfuture.UserManagement.SubscriptionPlan;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository repository;


    public List<SubscriptionPlan> getActivePlans() {
        return repository.findByActiveTrue();
    }

    public SubscriptionPlan createPlan(SubscriptionPlan plan) {
        return repository.save(plan);
    }

    public SubscriptionPlan updatePlan(Long id, SubscriptionPlan plan) {
        SubscriptionPlan existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        existing.setName(plan.getName());
        existing.setDurationDays(plan.getDurationDays());
        existing.setPrice(plan.getPrice());
        existing.setActive(plan.getActive());

        return repository.save(existing);
    }

    public List<SubscriptionPlan> getAllPlans(){
        return repository.findAll();
    }

    public void save(SubscriptionPlan plan){
        repository.save(plan);
    }

    public void togglePlan(Long id){

        SubscriptionPlan plan = repository.findById(id).orElseThrow();

        plan.setActive(!plan.getActive());

        repository.save(plan);
    }
}
