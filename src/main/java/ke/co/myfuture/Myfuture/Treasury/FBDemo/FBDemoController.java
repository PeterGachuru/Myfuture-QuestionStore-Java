package ke.co.myfuture.Myfuture.Treasury.FBDemo;

import ke.co.myfuture.Myfuture.Treasury.Account.Account;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("fbdemo")
public class FBDemoController {
    @Autowired
    FBDemoRepository fbDemoRepository;
    @PostMapping("add")
    public void newAccount(@RequestBody FBDemo fbDemo) {
         fbDemoRepository.save(fbDemo);
    }
}
