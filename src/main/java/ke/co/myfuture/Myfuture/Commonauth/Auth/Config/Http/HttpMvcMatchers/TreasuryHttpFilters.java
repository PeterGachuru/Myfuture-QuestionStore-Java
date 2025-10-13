package ke.co.myfuture.Myfuture.Commonauth.Auth.Config.Http.HttpMvcMatchers;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Role.AccessRight;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class TreasuryHttpFilters extends AbstractHttpConfigurer<TreasuryHttpFilters, HttpSecurity> {
    @Override
    public void init(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> {
                    auth.mvcMatchers("/treasury/**").authenticated();
//                                auth
//                        .mvcMatchers("/treasury/person/add").hasAnyAuthority(AccessRight.CREATE_PERSON.toString())
//                        .mvcMatchers("/treasury/person/update").hasAnyAuthority(AccessRight.CREATE_PERSON.toString())
//                        .mvcMatchers("/treasury/person/get/by/id").hasAnyAuthority(AccessRight.VIEW_PERSON.toString())
//                        .mvcMatchers("/treasury/person/all").hasAnyAuthority(AccessRight.VIEW_PERSON.toString())
//
//                        .mvcMatchers("/treasury/people-group/add").hasAnyAuthority(AccessRight.CREATE_PEOPLE_GROUP.toString())
//                        .mvcMatchers("/treasury/people-group/update").hasAnyAuthority(AccessRight.CREATE_PEOPLE_GROUP.toString())
//                        .mvcMatchers("/treasury/people-group/get/by/id").hasAnyAuthority(AccessRight.VIEW_PEOPLE_GROUP.toString())
//                        .mvcMatchers("/treasury/people-group/all").hasAnyAuthority(AccessRight.VIEW_PEOPLE_GROUP.toString())
//
//
//                        .mvcMatchers("/treasury/textreport/add").hasAnyAuthority(AccessRight.CREATE_FUNDING_COMPAIGN.toString())
//                        .mvcMatchers("/treasury/textreport/update").hasAnyAuthority(AccessRight.CREATE_FUNDING_COMPAIGN.toString())
//                        .mvcMatchers("/treasury/textreport/get/by/id").hasAnyAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.toString())
//                        .mvcMatchers("/treasury/textreport/all").hasAnyAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.toString())
//                        .mvcMatchers("/treasury/textreport/generate").hasAnyAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.toString())
//                        .mvcMatchers("/treasury/contribution-plan/add").hasAnyAuthority(AccessRight.CREATE_FUNDING_COMPAIGN.toString())
//                        .mvcMatchers("/treasury/contribution-plan/update").hasAnyAuthority(AccessRight.CREATE_FUNDING_COMPAIGN.toString())
//                        .mvcMatchers("/treasury/contribution-plan/all").hasAnyAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.toString())
//                        .mvcMatchers("/treasury/contribution-plan/get/by/id").hasAnyAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.toString())
//                        .mvcMatchers("/treasury/account/get/by/id").hasAnyAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.toString())
//                        .mvcMatchers("/treasury/account/all").hasAnyAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.toString())
//                        .mvcMatchers("/treasury/account/add").hasAnyAuthority(AccessRight.TRANSACT.toString())
//                        .mvcMatchers("/treasury/account/update").hasAnyAuthority(AccessRight.TRANSACT.toString())
//                        .mvcMatchers("/treasury/transaction/add").hasAnyAuthority(AccessRight.TRANSACT.toString())
//                        .mvcMatchers("/treasury/transaction/update").hasAnyAuthority(AccessRight.TRANSACT.toString())
//                        .mvcMatchers("/treasury/transaction/get/by/id").hasAnyAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.toString())
//                        .mvcMatchers("/treasury/transaction/all").hasAnyAuthority(AccessRight.VIEW_FUNDING_COMPAIGN.toString())
                });
    }
}