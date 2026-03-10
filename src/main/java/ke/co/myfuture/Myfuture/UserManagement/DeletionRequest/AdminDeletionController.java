package ke.co.myfuture.Myfuture.UserManagement.DeletionRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/deletion-requests")
public class AdminDeletionController {

    private final DeletionRequestRepo repository;

    public AdminDeletionController(DeletionRequestRepo repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {

        List<DeletionRequest> requests =
                repository.findAllByOrderByIdDesc(PageRequest.of(0,300));

        model.addAttribute("requests", requests);

        return "admin/deletion_requests";
    }

    @GetMapping("/mark-deleted/{id}")
    public String markDeleted(@PathVariable Long id) {

        DeletionRequest req = repository.findById(id).orElseThrow();

        req.setDateDeleted(new Date());

        repository.save(req);

        return "redirect:/admin/deletion-requests";
    }

}