package pl.adambaranowski.rsbackend.security.filter;

import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.adambaranowski.rsbackend.security.JpaUserDetailsService;
import pl.adambaranowski.rsbackend.security.jwt.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final String ACCOUNT_LOCKED_MESSAGE = "This account is locked, you can't use any secured endpoint";
    private static final String AUTHENTICATION_HEADER_NAME = "Authentication";
    private static final int TOKEN_PREFIX_LENGTH = "Bearer: ".length();
    private final JwtService jwtService;
    private final JpaUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {

            String authenticationHeader = httpServletRequest.getHeader(AUTHENTICATION_HEADER_NAME);

            System.out.println(authenticationHeader);

            if (authenticationHeader != null) {
                String jwt = authenticationHeader.substring(TOKEN_PREFIX_LENGTH);
                String username = jwtService.extractSubjectIfTokenIsValid(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (!userDetails.isAccountNonLocked()) {
                    throw new SecurityException(ACCOUNT_LOCKED_MESSAGE);
                }

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                userDetails.getUsername(),
                                null,
                                userDetails.getAuthorities()));
            }

            filterChain.doFilter(httpServletRequest, httpServletResponse);

        } catch (Exception e) {
            httpServletResponse.setStatus(403);
            httpServletResponse.getWriter().write(e.getMessage());
        }
    }
}