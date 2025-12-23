package ke.co.myfuture.Myfuture.Treasury.DashboardSupport;


import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("treasury/dashboard")
public class DashboardSupportController {
    @Autowired
    DashboardSupport dashboardSupport;
    @GetMapping("group/snapshot")
    public ResponseEntity<?> getSnapshotForGroup(@RequestParam("groupId") Long groupId) {
        return new ResponseEntity<>(dashboardSupport.getSnapshotForGroup(groupId), HttpStatus.OK);
    }

    @GetMapping("allplans/snapshot")
    public ResponseEntity<?> getSnapshotForAllPlans(@RequestParam("groupId") Long groupId) {
        return new ResponseEntity<>(dashboardSupport.getSnapshotForAllPlansByGroup(groupId), HttpStatus.OK);
    }

    @GetMapping("plan/snapshot")
    public ResponseEntity<?> getSnapshotForPlan(@RequestParam("planId") Long planId) {
        return new ResponseEntity<>(dashboardSupport.getSnapshotForPlan(planId), HttpStatus.OK);
    }
}
