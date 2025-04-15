package com.ecomerce.sb_ecom.security;

import com.ecomerce.sb_ecom.models.AppRole;
import com.ecomerce.sb_ecom.models.Role;
import com.ecomerce.sb_ecom.models.User;
import com.ecomerce.sb_ecom.repositories.RoleRepository;
import com.ecomerce.sb_ecom.repositories.UserRepository;
import com.ecomerce.sb_ecom.security.jwt.AuthEntryPointJwt;
import com.ecomerce.sb_ecom.security.jwt.AuthTokenFilter;
import com.ecomerce.sb_ecom.security.services.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    UserDetailServiceImpl userDetailServiceImpl;

    @Autowired
    private AuthEntryPointJwt authEntryPointJwt;

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider=new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)  throws Exception{
        return authConfig.getAuthenticationManager();
        /*It internally builds an AuthenticationManager using your config:
            Your UserDetailsServiceImpl

            Your PasswordEncoder

            Your DaoAuthenticationProvider*/
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf->csrf.disable())
                .exceptionHandling(exception->
                        exception.authenticationEntryPoint(authEntryPointJwt))
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->
                                auth.requestMatchers("/api/auth/**").permitAll()
                                        .requestMatchers("/v3/api-docs/").permitAll()
                                        .requestMatchers("/h2-console/**").permitAll()
                                        //.requestMatchers("/api/admin/**").permitAll()
                                        //.requestMatchers("/api/public/**").permitAll()
                                        .requestMatchers("/swagger-ui/**").permitAll()
                                        .requestMatchers("/api/test/**").permitAll()
                                        .requestMatchers("/images/**").permitAll()
                                        .anyRequest().authenticated()
                );
        http.authenticationProvider(authenticationProvider()); //modified DaoAuthenticationProvider
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.headers(headers->headers.frameOptions(
                frameOptions->frameOptions.sameOrigin()
        ));
        return http.build();
    }
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Retrieve or create roles
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRole.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });

            Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(AppRole.ROLE_SELLER);
                        return roleRepository.save(newSellerRole);
                    });

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);



        };
    }

    //allowing at global level
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/"));
    }

}
//Session Management :serverless state
/*
* Great question, Sachin! Let's break down **"stateless"** in the context of **Spring Security and JWT** – especially since you're using:

```java
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

---

### 🧠 What does **stateless** mean?

In simple terms:

> **Stateless** means the **server does not keep track of any user session** between requests.

Every HTTP request is **independent** and must **carry all the information** (like authentication) needed to process it.

---

### 🔄 What does **stateful** mean then?

> In **stateful**, the server maintains a **session (state)** for each client.

So once you log in, the server remembers you via a session ID stored in memory or cookies.

---

### ⚙️ Why use **stateless** in Spring Security with JWT?

Because:

1. **JWT tokens** carry all the necessary user info (userId, roles, etc.)
2. You don’t need to store anything on the server.
3. It's **scalable**, since server doesn't need to remember who is logged in.
4. Good for **REST APIs**.

So you tell Spring:
```java
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

→ This disables session creation, and Spring will **expect every request to carry its own authentication**, usually via:

```http
Authorization: Bearer <token>
```

---

### 🔐 What happens under the hood?

When you set:
```java
http.csrf().disable()
    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
```

- Spring doesn’t use session cookies.
- It doesn’t create or store `HttpSession`.
- Auth is handled by parsing your JWT on **every request** using your `AuthTokenFilter`.

---

### ✅ Summary

| Concept      | Stateful                              | Stateless                             |
|--------------|----------------------------------------|----------------------------------------|
| Storage      | Server stores session info             | Server stores nothing                  |
| Authentication | Done once and remembered via session | Done every time via JWT token          |
| Used in      | Web apps (with login, logout, etc.)    | REST APIs, microservices               |
| Scalability  | Harder to scale                        | Easy to scale (no session dependency)  |

---

Let me know if you want a small diagram or flow of **stateless login with JWT** 🔐✨
* */
