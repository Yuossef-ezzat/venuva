package com.example.venuva.Core.ServiceLayer;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.venuva.Core.Domain.Models.UserDetails.User;
import com.example.venuva.Infrastructure.PresistenceLayer.Repos.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerDetailsService implements UserDetailsService  {
    
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user;
    }
}