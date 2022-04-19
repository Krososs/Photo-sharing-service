package pl.sk.photosharingservice.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.sk.photosharingservice.support.AuthUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(request.getServletPath().equals("/login") ||  request.getServletPath().equals("/users/register") || request.getServletPath().equals("/users/token/refresh") ){
            filterChain.doFilter(request,response);
        }else{
            String authHeader = request.getHeader(AUTHORIZATION);
            if(authHeader!=null && authHeader.startsWith("e ")){
                try{
                    String token = authHeader.substring("e ".length());
                    DecodedJWT jwt = JWT.decode(token);

                    if( jwt.getExpiresAt().before(new Date())) {
                        Map<String, String> error = new HashMap<>();
                        error.put("error_message", "Token is expired");
                        response.setContentType(APPLICATION_JSON_VALUE);
                        response.setStatus(401);
                        new ObjectMapper().writeValue(response.getOutputStream(), error);
                    }

                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority(AuthUtil.getRolesFromToken(token)));
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jwt.getSubject(),null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);

                }catch (Exception exception){

                    response.setHeader("error", exception.getMessage());
                    response.setStatus(FORBIDDEN.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error_message",exception.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }

            }else{
                filterChain.doFilter(request, response);

            }
        }


    }
}
