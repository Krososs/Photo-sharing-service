package pl.sk.photosharingservice.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.sk.photosharingservice.support.AuthUtil;
import pl.sk.photosharingservice.support.ValidationUtil;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class AuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(request.getServletPath().equals("/login") ||  request.getServletPath().equals("/users/register") || request.getServletPath().equals("/users/token/refresh") ){
            filterChain.doFilter(request,response);
        }else{
            String authHeader = request.getHeader(AUTHORIZATION);
            if(authHeader!=null){
                try{
                    DecodedJWT jwt = JWT.decode(authHeader);

                    if( jwt.getExpiresAt().before(new Date())) {
                        response.setContentType(APPLICATION_JSON_VALUE);
                        response.setStatus(UNAUTHORIZED.value());
                        new ObjectMapper().writeValue(response.getOutputStream(), ValidationUtil.getErrorResponse(UNAUTHORIZED.value(), "Token is expired"));
                    }

                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority(AuthUtil.getRolesFromToken(authHeader)));
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jwt.getSubject(),null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);

                }catch (Exception exception){
                    response.setContentType(APPLICATION_JSON_VALUE);
                    response.setStatus(FORBIDDEN.value());
                    new ObjectMapper().writeValue(response.getOutputStream(), ValidationUtil.getErrorResponse(UNAUTHORIZED.value(), exception.getMessage()));
                }

            }else{
                filterChain.doFilter(request, response);

            }
        }
    }
}
