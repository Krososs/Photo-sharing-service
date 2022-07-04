package pl.sk.photosharingservice.support;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.stream.Collectors;

public class  AuthUtil {

    private final static Algorithm algorithm = Algorithm.HMAC256("secrect".getBytes());
    private final static JWTVerifier verifier = JWT.require(algorithm).build();

    public static String getAccesToken(UserDetails appUser){

        return JWT.create()
                .withSubject(appUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60*16)) //16h
                .withClaim("roles", appUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining()))
                .sign(algorithm);
    }

    public static String getRefreshToken(UserDetails appUser) {
        return JWT.create()
                .withSubject(appUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10000 * 60 * 60 * 100)) //100h
                .sign(algorithm);
    }

    public static String getUsernameFromToken(String token){
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }

    public static String getRolesFromToken(String token){
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getClaim("roles").toString();
    }



}
