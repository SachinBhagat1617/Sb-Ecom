package com.ecomerce.sb_ecom.controller;

import com.ecomerce.sb_ecom.models.AppRole;
import com.ecomerce.sb_ecom.models.Role;
import com.ecomerce.sb_ecom.models.User;
import com.ecomerce.sb_ecom.repositories.RoleRepository;
import com.ecomerce.sb_ecom.repositories.UserRepository;
import com.ecomerce.sb_ecom.security.jwt.JwtUtils;
import com.ecomerce.sb_ecom.security.payload.LoginRequest;
import com.ecomerce.sb_ecom.security.payload.LoginResponse;
import com.ecomerce.sb_ecom.security.payload.SignupRequest;
import com.ecomerce.sb_ecom.security.payload.SignupResponse;
import com.ecomerce.sb_ecom.security.services.UserDetailImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            //authentication  object contain
        }catch(AuthenticationException e){
            Map<String,Object>map=new HashMap<>(); // you want to return string:{object} like this
            map.put("message","Bad credentials");
            map.put("status",false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication); // authentication contains principal which is userDetailImpl and credentials as password
        UserDetailImpl userDetailImpl=(UserDetailImpl) authentication.getPrincipal();
        //String jwtToken= jwtUtils.generateTokenFromUsername(userDetail.getUsername());
        ResponseCookie cookie=jwtUtils.generateJwtCookie(userDetailImpl);
        List<String>roles=userDetailImpl.getAuthorities().stream()
                .map(item->item.getAuthority()).toList();
        LoginResponse loginResponse=new LoginResponse(userDetailImpl.getId(),userDetailImpl.getUsername(),roles);
        // set the cookie
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new SignupResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new SignupResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "seller":
                        Role modRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new SignupResponse("User registered successfully!"));
    }

    @GetMapping("/username")
    public String currentUserName(Authentication authentication){//authentication obj is automatically sent through api when useer is loggedIn
        if(authentication!=null){
            return authentication.getName();
        }
        return null;
    }

    @GetMapping("/user")
    public ResponseEntity<?> currentUser(Authentication authentication){
            UserDetailImpl userDetailImpl=(UserDetailImpl) authentication.getPrincipal();
            List<String>roles=userDetailImpl.getAuthorities().stream().map(item->item.getAuthority()).toList();
            LoginResponse loginResponse=new LoginResponse(userDetailImpl.getId(),userDetailImpl.getUsername(),roles);
            return ResponseEntity.ok().body(loginResponse);
    }
    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser(){
        ResponseCookie cookie=jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(new SignupResponse("Logout Successfully"));
    }

}
