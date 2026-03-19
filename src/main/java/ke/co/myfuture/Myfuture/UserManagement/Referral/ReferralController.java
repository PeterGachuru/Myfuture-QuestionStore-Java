package ke.co.myfuture.Myfuture.UserManagement.Referral;

import ke.co.myfuture.Myfuture.UserManagement.StudySubscription.StudySubscription;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("referral")
public class ReferralController {
    @Autowired
    ReferralService referralService;
    @Autowired
    ReferralRepository referralRepository;

    @PostMapping("add")//to be moved to installedApp
    public ResponseEntity<?> newStudentAccount(@RequestBody Referral referral) {
        if (referral.id != null) return null;

        Referral savedReferral = referralService.saveNewReffaral(referral, ReferralAction.INSTALLED);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedReferral);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("installedApp")//to be moved to installedApp
    public ResponseEntity<?> newStudentAccountInstall(@RequestBody Referral referral) {
        if (referral.id != null) return null;

        Referral savedReferral = referralService.saveNewReffaral(referral, ReferralAction.INSTALLED);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedReferral);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "share/{code}", produces = "text/html")
    public ResponseEntity<String> redirectWithPreview(@PathVariable String code) {
        referralService.linkClicked(code);

        // HTML with Open Graph tags for WhatsApp preview
        String html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta property="og:title" content="MyFuture CBC App - Learn Smartly!" />
            <meta property="og:description" content="Join MyFuture to access CBC learning materials and quizzes for all grades. Download now!" />
            <meta property="og:image" content="https://myfuture.co.ke/images/launcher-playstore.png" />
            <meta property="og:url" content="https://your-domain.com/referral/share/%s" />
            <meta name="twitter:card" content="summary_large_image" />
            <meta http-equiv="refresh" content="0;url=https://play.google.com/store/apps/details?id=ke.co.myfuture" />
        </head>
        <body>
            <p>Redirecting to Play Store...</p>
        </body>
        </html>
        """.formatted(code);

        return ResponseEntity.ok(html);
    }

    @GetMapping("forStudent")
    public ResponseEntity<List<Referral>> getSubscriptionListForParent(@RequestParam("student") Long student) {
        List<Referral> referrals = referralRepository.findByReferrerStudentId(student);
        return ResponseEntity.ok(referrals);
    }
}
