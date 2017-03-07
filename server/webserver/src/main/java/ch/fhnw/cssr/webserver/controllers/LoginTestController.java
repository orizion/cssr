package ch.fhnw.cssr.webserver.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpRequest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.cssr.security.CustomUserDetailsService;

@RestController
@RequestMapping("/logintest")
@ComponentScan(basePackageClasses = CustomUserDetailsService.class)
public class LoginTestController {

	public static class LoginData {
		private String email;
		private String password;

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
	}

	@Autowired
	private UserDetailsService manager;
	

	@RequestMapping(method = RequestMethod.POST)
	public void Login(@RequestBody LoginData lgdata) {

		UserDetails userDetails = manager.loadUserByUsername(lgdata.getEmail());
		UsernamePasswordAuthenticationToken authPwd = new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
				lgdata.getPassword(), userDetails.getAuthorities());


		SecurityContextHolder.getContext().setAuthentication(authPwd);
	}
}
