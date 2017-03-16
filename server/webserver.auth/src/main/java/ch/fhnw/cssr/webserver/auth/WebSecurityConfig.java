package ch.fhnw.cssr.webserver.auth;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import ch.fhnw.cssr.security.CustomPasswordEncoder;
import ch.fhnw.cssr.security.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = CustomUserDetailsService.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				// .antMatchers("/hello").access("hasRole('ROLE_ADMIN')")

				.anyRequest().authenticated()
				// .formLogin().usernameParameter("email").passwordParameter("password")
				.and().csrf().disable();
	}

	/**
	 * This section defines the user accounts which can be used for
	 * authentication as well as the roles each user has.
	 * 
	 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder)
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(new CustomPasswordEncoder());
	}
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() 
	throws Exception {
		DaoAuthenticationProvider p = new DaoAuthenticationProvider();
	    p.setUserDetailsService(userDetailsService);
	    p.setPasswordEncoder(new CustomPasswordEncoder());
	    List<AuthenticationProvider> ls = new ArrayList<AuthenticationProvider>();
	    ls.add(p);
	    return new ProviderManager(ls);
	}

}
