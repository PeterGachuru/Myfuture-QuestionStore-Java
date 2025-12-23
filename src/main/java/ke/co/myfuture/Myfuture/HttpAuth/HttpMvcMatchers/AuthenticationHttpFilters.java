package ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Role.AccessRight;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class AuthenticationHttpFilters extends AbstractHttpConfigurer<AuthenticationHttpFilters, HttpSecurity> {
    @Override
    public void init(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .mvcMatchers("/authentication/login").permitAll()
                        .mvcMatchers("/users/deleted-accounts").hasAnyAuthority(AccessRight.MODIFY_TOPIC.toString())
                        .mvcMatchers("/users/locked-accounts").hasAnyAuthority(AccessRight.MODIFY_TOPIC.toString())
                        .mvcMatchers("/users/analytics").hasAnyAuthority(AccessRight.MODIFY_TOPIC.toString())
                        .mvcMatchers("/powers/active-roles").hasAnyAuthority(AccessRight.MODIFY_TOPIC.toString())
                        .mvcMatchers("/powers/all-roles").hasAnyAuthority(AccessRight.MODIFY_TOPIC.toString())
                        .mvcMatchers("/powers/create-role").hasAnyAuthority(AccessRight.MODIFY_TOPIC.toString())
                        .mvcMatchers("/powers/update-role").hasAnyAuthority(AccessRight.MODIFY_TOPIC.toString())
                        .mvcMatchers("/users/create-user").hasAnyAuthority(AccessRight.CREATE_USER.toString())
                        .mvcMatchers("/users/register").permitAll()
                        .mvcMatchers("/users/update-user/:id").hasAnyAuthority(AccessRight.CREATE_USER.toString())
                        .mvcMatchers("/powers/activate-role").hasAnyAuthority(AccessRight.MODIFY_TOPIC.toString())
                        .mvcMatchers("/users/all-accounts").hasAnyAuthority(AccessRight.MODIFY_TOPIC.toString())
                        .mvcMatchers("/authentication/loginByRefreshToken").permitAll()
//                        .anyRequest().authenticated()
                );
    }
}
