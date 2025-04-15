package com.ecomerce.sb_ecom.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// intercepts invalid auth request , Provides Custom handling for unauthorised requests
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        logger.error("Unauthorised error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);  // it means you are sending JSON data type error format not an HTML error
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // setStatus to 401

        final Map<String,Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorised");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath()); //    /api/user/data

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);  // converts object to json [dest,src]
    }
}
