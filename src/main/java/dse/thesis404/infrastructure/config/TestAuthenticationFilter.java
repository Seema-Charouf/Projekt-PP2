package dse.thesis404.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class TestAuthenticationFilter extends OncePerRequestFilter {

    private static final String DEFAULT_TEST_USER = "1001";
    private static final String SESSION_USER_KEY = "TEST_USER_ID";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication existing = SecurityContextHolder.getContext().getAuthentication();
        if (existing != null && existing.getPrincipal() instanceof OAuth2User) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        

        String requestedUser = request.getParameter("testUser");
        if (requestedUser != null && isValidTestUser(requestedUser)) {
            session.setAttribute(SESSION_USER_KEY, requestedUser);
        }
        

        String currentUser = (String) session.getAttribute(SESSION_USER_KEY);
        if (currentUser == null) {
            currentUser = DEFAULT_TEST_USER;
            session.setAttribute(SESSION_USER_KEY, currentUser);
        }

        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(
                currentUser,
                null,
                List.of(
                    new SimpleGrantedAuthority("ROLE_USER"),
                    new SimpleGrantedAuthority("ROLE_BETREUER")
                )
            );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        filterChain.doFilter(request, response);
    }

    private boolean isValidTestUser(String userId) {

        return userId.matches("100[1-5]");
    }
}
