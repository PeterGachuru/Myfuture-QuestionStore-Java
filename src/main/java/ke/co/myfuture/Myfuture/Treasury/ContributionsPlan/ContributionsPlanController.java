package ke.co.myfuture.Myfuture.Treasury.ContributionsPlan;

import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Treasury.PersonGroup.PeopleGroup;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("treasury/contribution-plan")
public class ContributionsPlanController {
    @Autowired
    ContributionsPlanRepository repository;

    @Autowired
    ContributionsPlanService accountService;

    @PostMapping("add")
    public ResponseEntity<?> newContributionsPlan(@RequestBody ContributionsPlan account, @RequestParam("parentId") Long parentId) {
        UniversalResponse response = accountService.saveContributionsPlan(account, parentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateContributionsPlan(@RequestBody ContributionsPlan account, @RequestParam("parentId") Long parentId) {
        return new ResponseEntity<>(accountService.updateContributionsPlan(account, parentId), HttpStatus.OK);
    }
    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchContributionsPlan(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ContributionsPlan retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("all")
    public ResponseEntity<?> fetchProductCategory(@RequestParam(name = "parentId") Long parentId) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ProductCategory retrieved Successfully");

        List<ContributionsPlan> accountList;
        accountList = repository.findAllByAuditTrails_DeletedFlag(false, parentId);
        response.setEntity(accountList);
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("target-type")
    public ResponseEntity<?> getTargetType() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ProductCategory retrieved Successfully");
        response.setEntity(accountService.getTargetType());
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
