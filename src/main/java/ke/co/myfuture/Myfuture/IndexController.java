package ke.co.myfuture.Myfuture;

import ke.co.myfuture.Myfuture.UserManagement.PageVisit.VisitorSummary;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("")
public class IndexController {

    @GetMapping
    public String listVisitors() {
        return "index";
    }
    @GetMapping("privacypolicy.html")
    public String privacypolicy() {
        return "privacypolicy";
    }
}
