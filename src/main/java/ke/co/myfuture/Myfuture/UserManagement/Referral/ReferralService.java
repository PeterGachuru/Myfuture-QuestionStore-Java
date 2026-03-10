package ke.co.myfuture.Myfuture.UserManagement.Referral;


import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccountRepository;
import ke.co.myfuture.Myfuture.UserManagement.OldUseraccount.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReferralService {
    @Autowired
    ReferralRepository referralRepository;
    @Autowired
    IbukaStudentAccountRepository ibukaStudentAccountRepository;

    @Autowired
    UserAccountRepository userAccountRepository;

    public  String trimSpacesAndAsterisks(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("^[\\s*]+|[\\s*]+$", "");
    }


    public Referral saveNewReffaral(Referral referral, ReferralAction referralAction) {
        referral.referrerCode = trimSpacesAndAsterisks(referral.referrerCode);
        referral.referralAction = ReferralAction.INSTALLED;
        Optional<IbukaStudentAccount> referrer = ibukaStudentAccountRepository.findByShareCode(referral.referrerCode);

        if (referrer.isPresent()) {
            System.out.println("Found referrer "+ referral.referrerCode);
            referral.setReferrerEmail(referrer.get().getParentUsername());
            referral.setReferrerStudentId(referrer.get().getId());

            Integer currentCreditAmount = referrer.get().getCreditsBalance();
            currentCreditAmount += 20;
            referrer.get().setCreditsBalance(currentCreditAmount);

            ibukaStudentAccountRepository.save(referrer.get());
        }else {
            System.out.println("Did not find referrer "+ referral.referrerCode);
        }

        return referralRepository.save(referral);
    }

    public void linkClicked(String code) {
        Referral referral = new Referral();
        referral.referralAction = ReferralAction.LINK_CLICKED;
        referral.referrerCode = code;

        Optional<IbukaStudentAccount> referrer = ibukaStudentAccountRepository.findByShareCode(code);

        if (referrer.isPresent()) {
            System.out.println("Found referrer "+ referral.referrerCode);
            referral.setReferrerEmail(referrer.get().getParentUsername());
            referral.setReferrerStudentId(referrer.get().getId());

            if ( referrer.get().getCreditsBalance() == null)
                referrer.get().setCreditsBalance(0);

            Integer currentCreditAmount = referrer.get().getCreditsBalance();
            currentCreditAmount += 4;
            referrer.get().setCreditsBalance(currentCreditAmount);

            ibukaStudentAccountRepository.save(referrer.get());
        }else {
            System.out.println("Did not find referrer "+ referral.referrerCode);
        }

        referralRepository.save(referral);
    }
}