package com.shokr.Hotel.service;


import com.shokr.Hotel.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        @Transactional
        public UserDetails loadUserByUsername (String username) throws UsernameNotFoundException {
            return userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username/Email not found"));
        }
    }

