package com.ecomerce.sb_ecom.utils;

import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.models.User;
import com.ecomerce.sb_ecom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    @Autowired
    UserRepository userRepository;

    public String loggedInEmail(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUsername(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found with username: "+authentication.getName()));
        return user.getEmail();
    }

    public Long loggedInUserId() {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUsername(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found with username: "+authentication.getName()));
        return user.getUserId();
    }

    public User loggedInUser() {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUsername(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found with username: "+authentication.getName()));
        return user;
    }
}
