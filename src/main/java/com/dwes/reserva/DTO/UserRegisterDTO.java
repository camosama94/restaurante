package com.dwes.reserva.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterDTO {
    @NotEmpty(message = "El nombre de usuario es obligatorio")
    private String username;

    @Email(message = "El correo electrónico no es válido")
    @NotEmpty(message = "El correo electrónico es obligatorio")
    private String email;

    @NotEmpty(message = "La contraseña es obligatoria")
    private String password;

    @NotEmpty(message = "La confirmación de la contraseña es obligatoria")
    private String password2;

    @NotEmpty(message = "El teléfono es obligatorio")
    private String telefono;
}