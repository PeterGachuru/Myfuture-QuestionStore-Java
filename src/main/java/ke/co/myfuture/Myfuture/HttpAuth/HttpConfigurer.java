package ke.co.myfuture.Myfuture.HttpAuth;

import ke.co.myfuture.Myfuture.HttpAuth.HttpMvcMatchers.*;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.AuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


@Configuration
@EnableWebSecurity
@EnableWebMvc
public class HttpConfigurer  {

    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository;

    public HttpConfigurer(AuthenticationManager authenticationManager, SecurityContextRepository securityContextRepository) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling((exception) -> exception
                        .authenticationEntryPoint(((request, response, authException) -> {
                            response.setHeader("Access-Control-Allow-Origin", "*");
                            response.sendError(HttpStatus.BAD_REQUEST.value(), "Authorization failure. Kindly ensure that your session has not expired.");
                        }
                        ))
                        .accessDeniedHandler(((request, response, accessDeniedException) -> {
                            response.setHeader("Access-Control-Allow-Origin", "*");
                            response.sendError(HttpStatus.BAD_REQUEST.value(), "Access denied for the requested resource. Contact your system admin.");
                        })))
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .apply(new MyfutureNavigationHttpFilters()).and()
                .apply(new DukazoteHttpFilters()).and()
                .apply(new MyScpHttpFilters()).and()
                .apply(new ImageStoreHttpFilters()).and()
                .apply(new TreasuryHttpFilters()).and()
                .apply(new AuthenticationHttpFilters()).and()
                .authorizeHttpRequests((auth) -> auth
                                // PERMIT ALL OPTIONS REQUEST
                                .mvcMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authenticationManager(authenticationManager)
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public InternalResourceViewResolver defaultViewResolver() {
        return new InternalResourceViewResolver();
    }
}
