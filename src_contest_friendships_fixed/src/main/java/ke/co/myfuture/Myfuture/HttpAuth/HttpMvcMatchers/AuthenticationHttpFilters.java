package ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers;

import ke.co.myfuture.Myfuture.Commonauth.Auth.RoleConfig.AccessRight;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class AuthenticationHttpFilters extends AbstractHttpConfigurer<AuthenticationHttpFilters, HttpSecurity> {

    @Override
    public void init(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth

                // Public endpoints
                .requestMatchers("/authentication/login").permitAll()
                .requestMatchers("/users/register").permitAll()
                .requestMatchers("/authentication/loginByRefreshToken").permitAll()

                // User management
                .requestMatchers("/users/deleted-accounts")
                .hasAuthority(AccessRight.MODIFY_TOPIC.name())

                .requestMatchers("/users/locked-accounts")
                .hasAuthority(AccessRight.MODIFY_TOPIC.name())

                .requestMatchers("/users/analytics")
                .hasAuthority(AccessRight.MODIFY_TOPIC.name())

                .requestMatchers("/users/all-accounts")
                .hasAuthority(AccessRight.MODIFY_TOPIC.name())

                // Role / power management
                .requestMatchers("/powers/active-roles")
                .hasAuthority(AccessRight.MODIFY_TOPIC.name())

                .requestMatchers("/powers/all-roles")
                .hasAuthority(AccessRight.MODIFY_TOPIC.name())

                .requestMatchers("/powers/create-role")
                .hasAuthority(AccessRight.MODIFY_TOPIC.name())

                .requestMatchers("/powers/update-role")
                .hasAuthority(AccessRight.MODIFY_TOPIC.name())

                .requestMatchers("/powers/activate-role")
                .hasAuthority(AccessRight.MODIFY_TOPIC.name())

                // User creation
                .requestMatchers("/users/create-user")
                .hasAuthority(AccessRight.CREATE_USER.name())

                // FIX: Spring does NOT support ":id"
                // Use wildcard instead
                .requestMatchers("/users/update-user/**")
                .hasAuthority(AccessRight.CREATE_USER.name())
        );
    }

    @Override
    public void configure(HttpSecurity http) {
        // required override for AbstractHttpConfigurer (safe empty)
    }
}