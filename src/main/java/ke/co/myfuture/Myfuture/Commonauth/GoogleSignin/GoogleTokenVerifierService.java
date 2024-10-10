package ke.co.myfuture.Myfuture.Commonauth.GoogleSignin;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;

@Service
public class GoogleTokenVerifierService {
    @Value("${auth.auth2ClientId}")
    private String auth2ClientId;
    public GoogleIdToken.Payload validateGoogleIdToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance())
                // Specify the CLIENT_ID of the app that accesses the backend
                .setAudience(Collections.singletonList(auth2ClientId))
                .build();

        // Verify the ID token
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            return idToken.getPayload();
        } else {
            throw new Exception("Invalid ID Token");
        }
    }
}
