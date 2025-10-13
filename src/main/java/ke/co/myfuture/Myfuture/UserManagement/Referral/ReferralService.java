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

    public Referral save(Referral referral) {
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
}