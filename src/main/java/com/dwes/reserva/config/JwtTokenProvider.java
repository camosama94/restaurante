package com.dwes.reserva.config;

import com.dwes.reserva.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.util.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;


import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private static final String SECRET_KEY = "SuperClaveSecretaParaElJwtCon32CaracteresMin";
    private static final long EXPIRATION_TIME = 86400000; // 1 día


    public String  generateToken(Authentication authentication) {

        UserEntity user = (UserEntity) authentication.getPrincipal();
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        // Obtener los roles desde la relación con UserAuthority
        String roles = String.join(",", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Convertir cada GrantedAuthority a String (role)
                .toList());

        return Jwts.builder()
                .subject(Long.toString(user.getId()))
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key) // Firma con el algoritmo por defecto
                .compact();
    }

    //Validar firma del token
    public boolean isValidToken(String token) {
        if(StringUtils.isBlank(token)){
            return false;
        }

        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        try {
            JwtParser validator = Jwts.parser()
                    .verifyWith(key)
                    .build();
            validator.parse(token);
            return true;
        }catch (Exception e){
            //Aquí entraría si el token no fuera correcto o estuviera caducado.
            // Podríamos hacer un log de los fallos
            System.err.println("Error al validar el token: " + e.getMessage());
            return false;
        }

    }

    public String getUsernameFromToken(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build();
        Claims claims = parser.parseClaimsJws(token).getBody();
        return claims.get("username").toString();
    }


    public String getRolesFromToken(String token) {
        Claims claims = Jwts.parser()  // Usamos parser() como antes
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))  // Establecemos la clave secreta
                .build()  // Es necesario llamar a build() para crear el parser
                .parseSignedClaims(token)  // Usamos parseSignedClaims en lugar de parseClaimsJws
                .getPayload();  // Extraemos el cuerpo del JWT

        return claims.get("roles", String.class);  // Devolvemos el rol como una cadena
    }


    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject()); // El "sub" del JWT es el ID del usuario por convención
    }


}