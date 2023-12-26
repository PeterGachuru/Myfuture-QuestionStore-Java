package ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities;

import java.io.Serializable;
import java.util.Random;

public class PasswordGenerator implements Serializable {
    public String generatePassword() {
        String characters = "01234ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz56789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        while (sb.length() < 8) {
            int index = (int) (rnd.nextFloat() * characters.length());
            sb.append(characters.charAt(index));
        }
        String s = sb.toString();
        return s;
    }


    public int generatePassResetToken()
    {
        Random rn = new Random();
        int token = rn.nextInt(1000000 - 110 + 1) + 2;
        return token;
    }
}
