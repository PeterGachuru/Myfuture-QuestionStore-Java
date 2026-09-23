package ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers;

import ke.co.myfuture.Myfuture.Commonauth.Auth.RoleConfig.AccessRight;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class MyfutureNavigationHttpFilters extends AbstractHttpConfigurer<MyfutureNavigationHttpFilters, HttpSecurity> {

    @Override
    public void init(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth

                // Public endpoints
                .requestMatchers("/curriculums/all/minimal").permitAll()
                .requestMatchers("/curriculums/all").permitAll()
                .requestMatchers("/classlevel/getbyid").permitAll()
                .requestMatchers("/topic/get/by/subjectandclass").permitAll()
                .requestMatchers("/topic/get/by/parent").permitAll()
                .requestMatchers("/topic/get/by/id").permitAll()
                .requestMatchers("/reports/load").permitAll()
                .requestMatchers("/users/all-accounts").permitAll()

                // Protected endpoints
                .requestMatchers("/topic/update")
                .hasAuthority(AccessRight.MODIFY_TOPIC.name())

                .requestMatchers("/questionstore/questions/approve")
                .hasAuthority(AccessRight.APPROVE_QUESTION.name())

                .requestMatchers("/questionstore/questions/delete")
                .hasAuthority(AccessRight.DELETE_QUESTION.name())
        );
    }

    @Override
    public void configure(HttpSecurity http) {
        // required override for AbstractHttpConfigurer
    }
}