package com.dwes.reserva.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }


    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder,
                                                       UserDetailsService userDetailsService) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())   //Se desabilita para las API ya que no se manejan sesiones sino con tokens
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Indicamos que no cree una sesión porque vamos a utilizar tokens
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**","/v3/api-docs/**","/swagger-ui/**").permitAll()
                        .requestMatchers("/mesas","/mesas/**","/clientes","/clientes/**").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers("/reservas", "/reservas/**").hasAnyAuthority("ROLE_USER")
                        .anyRequest().authenticated()
                );


        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); //Añadimos un filtro que intercepta cada petición HTTP para obtener el token JWK y validarlo

        return http.build();
    }
}
