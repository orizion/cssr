package ch.fhnw.cssr.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.io.IOException;
import java.util.Collection;

class TokenAuthenticationService {
	static final long EXPIRATIONTIME = 3_600_000; // 60 Minutes
	static final String SECRET = "ThisIsASecret";
	static final String TOKEN_PREFIX = "Bearer";
	static final String HEADER_STRING = "Authorization";
	static final String ROLES_KEY = "r";
	static final SignatureAlgorithm Algorithm = SignatureAlgorithm.HS512;
	static final String JwtHeader = "eyJhbGciOiJIUzUxMiJ9";
	
	
	static void addAuthentication(HttpServletResponse res, Collection<? extends GrantedAuthority> authorities,
			String username) throws IOException {
		JwtBuilder builder = Jwts.builder();

		Claims s = new DefaultClaims();
		String rolesStr = String.join(",", AuthorityUtils.authorityListToSet(authorities));
		s.put(ROLES_KEY, rolesStr);
		s.setSubject(username);
		s.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME));
		builder.setClaims(s);
		String JWT = builder.signWith(Algorithm, SECRET).compact();
		
		// Set the first header bits to the JwtHeader Field
		JWT = JWT.substring(JWT.indexOf('.')+1) ; // Remove headers, as this is often a cause for weak security
		
		res.setContentType("application/json");
		res.getWriter().write("{ \"token\": \"" + JWT + "\" }");
		// res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
	}

	static Authentication getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING);
		if (token != null) {
			// parse the token.
			Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET)
					.parseClaimsJws(JwtHeader + "." + token.replace(TOKEN_PREFIX, "").trim());
			if (claims.getHeader().getAlgorithm().equals("none")) {
				throw new IllegalArgumentException("Algorithm not valid");
			}
			Claims jwtBody = claims.getBody();
			String roleList = (String) jwtBody.get(ROLES_KEY);
			List<GrantedAuthority> roles = AuthorityUtils.commaSeparatedStringToAuthorityList(roleList);

			String user = jwtBody.getSubject();

			return user != null ? new UsernamePasswordAuthenticationToken(user, null, roles) : null;
		}
		return null;
	}
}