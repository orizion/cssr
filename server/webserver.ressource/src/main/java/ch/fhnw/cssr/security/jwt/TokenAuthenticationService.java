package ch.fhnw.cssr.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

class TokenAuthenticationService {
	static final long EXPIRATIONTIME = 3_600_000; // 60 Minutes
	static final String SECRET = "ThisIsASecret";
	static final String TOKEN_PREFIX = "Bearer";
	static final String HEADER_STRING = "Authorization";
	static final String ROLES_KEY = "r";

	static void addAuthentication(HttpServletResponse res, Collection<? extends GrantedAuthority> authorities, String username) throws IOException {
		JwtBuilder builder = Jwts.builder();
		
 		Claims s = new DefaultClaims();
 		String rolesStr = String.join(",", AuthorityUtils.authorityListToSet(authorities));
 		s.put(ROLES_KEY, rolesStr);
 		s.setSubject(username);
 		s.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME));
		builder.setClaims(s);
		String JWT = builder.signWith(SignatureAlgorithm.HS512, SECRET).compact();
		res.setContentType("application/json");
		res.getWriter().write("{ \"token\": \"" + JWT + "\" }");
		// res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
	}

	static Authentication getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING);
		if (token != null) {
			// parse the token.
			Claims jwtBody = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "").trim())
					.getBody();
			String roleList = (String) jwtBody.get(ROLES_KEY);
			List<GrantedAuthority> roles = AuthorityUtils.commaSeparatedStringToAuthorityList(roleList); 

			String user = jwtBody.getSubject();

			return user != null ? new UsernamePasswordAuthenticationToken(user, null, roles)
					: null;
		}
		return null;
	}
}