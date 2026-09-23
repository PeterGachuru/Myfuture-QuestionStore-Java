package ke.co.myfuture.Myfuture.MpesaIntegration;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/mpesa-transactions")
public class AdminMpesaTransactionController {

    private final MpesaTransactionRepository repository;

    public AdminMpesaTransactionController(MpesaTransactionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {

        List<MpesaTransaction> transactions =
                repository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 300));

        model.addAttribute("transactions", transactions);

        return "admin/mpesa_transactions";
    }
}