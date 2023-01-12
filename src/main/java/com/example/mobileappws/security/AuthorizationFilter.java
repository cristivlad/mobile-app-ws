package com.example.mobileappws.security;

import com.example.mobileappws.entity.UserEntity;
import com.example.mobileappws.repository.user.UserRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.mobileappws.security.SecurityConstants.*;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;

    public AuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
            token = token.replace(TOKEN_PREFIX, "");

            String user = Jwts.parser()
                    .setSigningKey(getTokenSecret())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            if (user != null) {
                UserEntity userEntity = userRepository.findByEmail(user);
                UserPrincipal userPrincipal = new UserPrincipal(userEntity);
                return  new UsernamePasswordAuthenticationToken(user, null, userPrincipal.getAuthorities());
            }
            return null;
        }
        return null;
    }
}
