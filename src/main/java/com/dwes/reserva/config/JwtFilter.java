package com.dwes.reserva.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.extractToken(request);

        if (token != null && this.tokenProvider.isValidToken(token)) {
            // Extraer el username y los roles del token
            String username = this.tokenProvider.getUsernameFromToken(token);
            /*String roles = this.tokenProvider.getRolesFromToken(token);  // Método para obtener roles del JWT

            // Convertir los roles en objetos GrantedAuthority
            Collection<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                    .map(role -> (GrantedAuthority) () -> "ROLE_" + role.trim())  // Crear un GrantedAuthority para cada rol
                    .collect(Collectors.toList());*/

            String roles = this.tokenProvider.getRolesFromToken(token);  // Método para obtener roles del JWT

            System.out.println("Token: " + token);
            System.out.println("Roles: " + roles);

            Collection<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                    .map(role -> (GrantedAuthority) () -> role)  // ROLE_USER, no ROLE_USER, sin el prefijo 'ROLE_'
                    .collect(Collectors.toList());



            // Crear el objeto de autenticación
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,  // No necesitamos la contraseña porque estamos usando JWT
                    authorities
            );

            // Establecer los detalles de la autenticación
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Establecer el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continuar con el filtro
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        System.out.println(bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token =  bearerToken.substring(7);  // Eliminar "Bearer " y obtener el token
            System.out.println(token);
            return token;
        }
        return null;
    }


}
