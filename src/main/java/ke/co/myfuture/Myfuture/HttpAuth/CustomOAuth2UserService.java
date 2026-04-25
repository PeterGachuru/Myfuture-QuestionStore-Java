package ke.co.myfuture.Myfuture.HttpAuth;

import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.UserPasswords.UserPassword;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oauthUser = super.loadUser(userRequest);

        String email = oauthUser.getAttribute("email");
        String firstName = oauthUser.getAttribute("given_name");
        String lastName = oauthUser.getAttribute("family_name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(email, firstName, lastName));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                oauthUser.getAttributes(),
                "email"
        );
    }

    private User registerNewUser(String email, String firstName, String lastName) {

        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setStatus("Active");

        // Generate random password (not used but required)
        String randomPassword = UUID.randomUUID().toString();
        UserPassword userPassword = new UserPassword();
        userPassword.setPassword(passwordUtil.encode(randomPassword));

        user.setPasswords(List.of(userPassword));
        user.setFirstLogin(0);

        return userRepository.save(user);
    }
}
