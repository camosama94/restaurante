package com.dwes.reserva.controller;

import com.dwes.reserva.DTO.LoginRequestDTO;
import com.dwes.reserva.DTO.LoginResponseDTO;
import com.dwes.reserva.DTO.UserRegisterDTO;
import com.dwes.reserva.config.JwtTokenProvider;
import com.dwes.reserva.entity.Cliente;
import com.dwes.reserva.entity.UserEntity;
import com.dwes.reserva.repository.ClienteRepository;
import com.dwes.reserva.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AuthController {
    @Autowired
    private UserEntityRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("/auth/register")
    public ResponseEntity<?> save(@RequestBody UserRegisterDTO userDTO) {



        UserEntity userEntity = this.userRepository.save(
                UserEntity.builder()
                        .username(userDTO.getUsername())
                        .password(passwordEncoder.encode(userDTO.getPassword()))
                        .email(userDTO.getEmail())
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")).stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                        .build());

        // Crear el cliente asociado
        Cliente cliente = Cliente.builder()
                .nombre(userDTO.getUsername())
                .email(userDTO.getEmail())
                .telefono(userDTO.getTelefono())
                .userEntity(userEntity)
                .build();

        // Guardar el cliente
        this.clienteRepository.save(cliente);

        return ResponseEntity.status(HttpStatus.CREATED).body(userEntity);

    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDTO) {
        try {

            //Validamos al usuario en Spring (hacemos login manualmente)
            UsernamePasswordAuthenticationToken userPassAuthToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
            Authentication auth = authenticationManager.authenticate(userPassAuthToken);    //valida el usuario y devuelve un objeto Authentication con sus datos
            //Obtenemos el UserEntity del usuario logueado
            UserEntity user = (UserEntity) auth.getPrincipal();

            //Generamos un token con los datos del usuario (la clase tokenProvider ha hemos creado nosotros para no poner aquí todo el código
            String token = this.tokenProvider.generateToken(auth);

            //Devolvemos un código 200 con el username y token JWT
            return ResponseEntity.ok(new LoginResponseDTO(user.getUsername(), token));
        }catch (Exception e) {  //Si el usuario no es válido, salta una excepción BadCredentialsException
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of(
                            "path", "/auth/login",
                            "message", "Credenciales erróneas",
                            "timestamp", new Date()
                    )
            );
        }
    }


}