package com.ecomerce.sb_ecom.security.services;

import com.ecomerce.sb_ecom.models.User;
import com.ecomerce.sb_ecom.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user=userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with username"+username));
        return UserDetailImpl.build(user);
    }

}
