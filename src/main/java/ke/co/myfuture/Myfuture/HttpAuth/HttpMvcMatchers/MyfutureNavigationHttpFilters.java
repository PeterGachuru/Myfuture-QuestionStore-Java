package ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Role.AccessRight;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class MyfutureNavigationHttpFilters extends AbstractHttpConfigurer<MyfutureNavigationHttpFilters, HttpSecurity> {
    @Override
    public void init(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .mvcMatchers("/curriculums/all/minimal").permitAll()
                        .mvcMatchers("/curriculums/all").permitAll()
                        .mvcMatchers("/classlevel/getbyid").permitAll()
                        .mvcMatchers("/topic/get/by/subjectandclass").permitAll()
                        .mvcMatchers("/topic/get/by/parent").permitAll()
                        .mvcMatchers("/topic/get/by/id").permitAll()
                        .mvcMatchers("/reports/load").permitAll()
                        .mvcMatchers("/users/all-accounts").permitAll()
                        .mvcMatchers("/topic/update").hasAnyAuthority(AccessRight.MODIFY_TOPIC.toString())
                        .mvcMatchers("/questionstore/questions/approve").hasAnyAuthority(AccessRight.APPROVE_QUESTION.toString())
                        .mvcMatchers("/questionstore/questions/delete").hasAnyAuthority(AccessRight.DELETE_QUESTION.toString())
                );
    }
}
