package ge.bestline.delivery.ws.security.jwt;

import ge.bestline.delivery.ws.Exception.RestResponse;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class JwtTokenFilter extends GenericFilterBean {

    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        try {
            String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
            jwtTokenProvider.validateToken(token);
            filterChain.doFilter(req, res);
        } catch (Exception e) {
            HttpServletResponse response = (HttpServletResponse) res;
            HttpServletRequest request = (HttpServletRequest) req;
            String message = RestResponse.builder().status(FORBIDDEN).message("Invalid Authorization token").path(request.getRequestURI()).json();
            response.setStatus(UNAUTHORIZED.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.getWriter().write(message);
        }

    }

}
