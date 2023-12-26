package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.services;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Users.Users;
import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Users.UsersRepository;
import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.utils.HttpInterceptor.UserDetailsRequestContext;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
    UsersRepository usersRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = usersRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		    Gson g = new Gson();
			UserDetailsRequestContext.setCurrentUserDetails(g.toJson(user));
		return UserDetailsImpl.build(user);
	}
}
