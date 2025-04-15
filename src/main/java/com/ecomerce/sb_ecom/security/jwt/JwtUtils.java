package com.ecomerce.sb_ecom.security.jwt;

import com.ecomerce.sb_ecom.security.services.UserDetailImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret; //value will be assigned from application.properties

    @Value("${spring.app.jwtExpirations}")
    private int jwtExpirations;

//    public String getJwtFromHeader(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        logger.debug("Authorization Header: {}", bearerToken);
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7); // Remove Bearer prefix
//        }
//        return null;
//    }
    public String getJwtFromCookie(HttpServletRequest request) {
        Cookie cookie= WebUtils.getCookie(request,"jwtcookie");
        if(cookie!=null){
            return cookie.getValue();
        }
        return null;
    }
    public ResponseCookie generateJwtCookie(UserDetailImpl userDetailsImpl) {
        //generate token from username
        String username = userDetailsImpl.getUsername();
        String token=generateTokenFromUsername(username);
        // generate cookie with help of token and set path api so that this cookie will be saved to all the path with api
        ResponseCookie cookie=ResponseCookie.from("jwtcookie",token).path("/api")
                .maxAge(24*60*60)
                .httpOnly(false)
                .build();
        return cookie;
    }
    public ResponseCookie getCleanJwtCookie(){
        ResponseCookie cookie=ResponseCookie.from("jwtcookie",null).path("/api").build();
        return cookie;
    }
    public String generateTokenFromUsername(String username) {
        //String username = userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirations))
                .signWith(key())
                .compact();
    }
    //new Date(); // Example output: Fri Mar 29 2025 12:00:00 GMT+0000
    //(new Date()).getTime(); // Example output: 1743451200000 (milliseconds)
    //new Date(1743451200000); // Converts timestamp back to date

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }


    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public boolean validateJwtToken(String authToken) {
        try {
            System.out.println("Validate");
            // we have to verify with hashed secret ley because in authentication also jwt token contains hashed secret Key
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
