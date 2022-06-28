package ge.bestline.delivery.ws.security.jwt;

import ge.bestline.delivery.ws.Exception.RestResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class JwtTokenFilter extends OncePerRequestFilter {
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.getAuthentication(token));
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            String ipAddress = req.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = req.getRemoteAddr();
            }
            System.out.println("Request Was Sent From: " + ipAddress);
            e.printStackTrace();
            HttpServletResponse response = (HttpServletResponse) res;
            HttpServletRequest request = (HttpServletRequest) req;
            String message = RestResponse.builder().status(FORBIDDEN).message("Invalid Authorization token").path(request.getRequestURI()).json();
            response.setStatus(UNAUTHORIZED.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.getWriter().write(message);
        }
        filterChain.doFilter(req, res);
    }

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
}
