package ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/emails")
public class AdminScheduledEmailsController {

    private static final int PAGE_SIZE = 50;

    private final ScheduledEmailsRepo repository;

    public AdminScheduledEmailsController(ScheduledEmailsRepo repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listEmails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sent,
            @RequestParam(required = false) String email,
            Model model) {

        int safePage = Math.max(0, page);
        String selectedCategory = normalizeCategory(category);
        Boolean selectedSent = normalizeSent(sent);

        String selectedEmail = normalizeEmail(email);

        Page<ScheduledEmailsRepo.ScheduledEmailsProjection> emails = repository.findAdminEmails(
                selectedCategory, selectedSent, selectedEmail, PageRequest.of(safePage, PAGE_SIZE));

        // If a stale page number is requested after records are removed, return to the last page.
        if (emails.getTotalPages() > 0 && safePage >= emails.getTotalPages()) {
            safePage = emails.getTotalPages() - 1;
            emails = repository.findAdminEmails(selectedCategory, selectedSent, selectedEmail,
                    PageRequest.of(safePage, PAGE_SIZE));
        }

        model.addAttribute("emails", emails);
        model.addAttribute("currentPage", safePage);
        model.addAttribute("selectedCategory", selectedCategory == null ? "" : selectedCategory);
        model.addAttribute("selectedSent", selectedSent == null ? "" : selectedSent.toString());
        model.addAttribute("selectedEmail", selectedEmail == null ? "" : selectedEmail);
        model.addAttribute("categories", repository.findDistinctSenderServices());
        return "admin/emails";
    }

    /**
     * Serves the stored email body for display inside a sandboxed iframe.
     * The iframe must not grant scripts or same-origin privileges.
     */
    @GetMapping(value = "/{id}/preview", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ResponseEntity<String> emailPreview(@PathVariable Long id) {
        ScheduledEmails email = repository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Email not found"));

        String body = email.getBody() == null ? "" : email.getBody();
        String trimmed = body.trim().toLowerCase(java.util.Locale.ROOT);
        boolean isHtml = trimmed.startsWith("<!doctype")
                || trimmed.startsWith("<html")
                || trimmed.contains("<body")
                || trimmed.contains("<table")
                || trimmed.contains("<div")
                || trimmed.contains("<p")
                || trimmed.contains("<style");

        // Plain-text emails are escaped before being wrapped; HTML emails are
        // returned unchanged so full documents, including <!doctype html>, render correctly.
        String htmlBody = isHtml ? body
                : "<!doctype html><html><head><meta charset=\"UTF-8\"></head>"
                + "<body><pre style=\"white-space:pre-wrap;overflow-wrap:anywhere;font-family:Arial,sans-serif;\">"
                + HtmlUtils.htmlEscape(body)
                + "</pre></body></html>";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "html", java.nio.charset.StandardCharsets.UTF_8));
        headers.set("Content-Security-Policy",
                "default-src 'none'; img-src https: http: data:; style-src 'unsafe-inline' https: http:; "
                + "font-src https: http: data:; media-src https: http:; base-uri 'none'; "
                + "form-action 'none'; frame-ancestors 'self'");
        headers.set("X-Content-Type-Options", "nosniff");
        headers.set("Cache-Control", "no-store");
        return ResponseEntity.ok().headers(headers).body(htmlBody);
    }

    @GetMapping("/{id}")
    public String emailDetails(@PathVariable Long id, Model model) {
        ScheduledEmails email = repository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Email not found"));
        model.addAttribute("email", email);
        return "admin/email-details";
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) return null;
        String normalized = category.trim();
        if (normalized.length() > 100) return null;
        return normalized;
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) return null;
        return email.trim();
    }

    private Boolean normalizeSent(String sent) {
        if (sent == null || sent.isBlank()) return null;
        if ("true".equalsIgnoreCase(sent)) return Boolean.TRUE;
        if ("false".equalsIgnoreCase(sent)) return Boolean.FALSE;
        return null;
    }
}
