package app.services;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class GoogleUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		if (username != "wingfei.siew@redtone.com") {
			Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN","role2","role3");
		    return new User(username, "###", authorities);
		}
		
		else {
			return null;
		}
	}
}
