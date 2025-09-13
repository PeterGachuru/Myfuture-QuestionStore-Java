package ke.co.myfuture.Myfuture.Commonauth.Auth.Otp;

import ke.co.myfuture.Myfuture.Commonauth.CustomerExceptions.MaximumRetriesException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@AllArgsConstructor
@Slf4j
public class    OtpService {
    private final OtpRepository otpRepository;

    public String generateOTP(String email, String jwt){
        invalidateAllOtps(email);
        String otp =  generateOTP();
        Otp otpObj = new Otp();
        otpObj.setJwt(jwt);
        otpObj.setEmail(email);
        otpObj.setIsValid(true);
        otpObj.setRetries(0);
        otpObj.setOtpValue(otp);
        otpRepository.save(otpObj);

        log.info("Generating otp {}", otp);
        return otp;
    }

    public String generateOTP(String email) throws ChangeSetPersister.NotFoundException {
        List<Otp> otps = otpRepository.findTopByEmailOrderByIdDesc(email);
        if (otps.isEmpty()){
            throw new ChangeSetPersister.NotFoundException();
        }
        Otp otp = otps.get(0);
        String otpValue = generateOTP();
        otp.setOtpValue(otpValue);
        otp.setIsValid(false);
        otpRepository.save(otp);
        return otpValue;
    }

    public void saveToken(String email, String jwt){
        deletePreviousTokens(email);
        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpValue(null);
        otp.setJwt(jwt);
        otp.setRetries(0);
        otp.setIsValid(true);
        otpRepository.save(otp);
    }

    public Boolean validateOtp(String email, String otp) throws MaximumRetriesException{
        List<Otp> lastOtp = otpRepository.findTopByEmailOrderByIdDesc(email);
        if (lastOtp.isEmpty()){
            return false;
        }else{
            Otp otpObj = lastOtp.get(0);

            if (otpObj.getRetries() >= 5){
                throw new MaximumRetriesException();
            }

            boolean status = otpObj.getIsValid() && !Objects.isNull(otpObj.getOtpValue()) && otpObj.getOtpValue().equals(otp);

            if (status) {
                invalidateAllOtps(email);
            } else {
                otpObj.setRetries(otpObj.getRetries() + 1);
            }

            return status;
        }
    }
    public void invalidateAllOtps(String email){
        List<Otp> otps = otpRepository.findAllByEmail(email);
        otps = otps.stream().peek(otp -> otp.setIsValid(false)).toList();
        otpRepository.saveAll(otps);
    }
    public void deletePreviousTokens(String email){
        this.otpRepository.deleteAllByEmail(email);
    }

    public boolean validateLoginRetries(String email){
        System.out.println("In validateLoginRetries");
        List<Otp> lastSignin = otpRepository.findTopByEmailOrderByIdDesc(email);
        System.out.println("Got last logins from db");
        Otp otp;
        if (lastSignin.isEmpty() || !Objects.isNull(lastSignin.get(0).getJwt())){
            System.out.println("lastSignin is null");
            otp = new Otp();
            otp.setRetries(1);
            otp.setEmail(email);
        }else{
            System.out.println("lastSignin is no empty");
            otp = lastSignin.get(0);
            otp.setRetries(otp.getRetries() + 1);
        }

        System.out.println("After empty check");

        if (otp.getRetries() >= 15){
            System.out.println("Out of validateLoginRetries");
            return false;
        } else {
            System.out.println("Before saving otp");
            otpRepository.save(otp);
            System.out.println("Out of validateLoginRetries");
            return true;
        }
    }

    public void resetAllRetries(String email){
        otpRepository.updateRetries(email);
    }
    public String generateOTP() {
        String numbers = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rndm_method = new Random();
        char[] otp = new char[6];
        for (int i = 0; i < 6; i++) {
            otp[i] = numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
        return new String(otp);
    }

    public Boolean validateJwtToken(String jwt) {
        log.info("Validating");
        return !this.otpRepository.findByJwt(jwt).isEmpty();
//        return true;
    }
}
