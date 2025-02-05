package com.dwes.reserva.service;

import com.dwes.reserva.repository.UserEntityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserEntityRepository userRepository;

    UserDetailsServiceImpl(UserEntityRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Extrae el usuario de la BD
        return this.userRepository.findByUsername(username).orElseThrow(
                ()-> new UsernameNotFoundException(username+" no encontrado")
        );

    }
}