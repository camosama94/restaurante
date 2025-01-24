package com.dwes.reserva.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message="El nombre no puede estar en blanco")
    @Length(min=3, message="El nombre debe contener 3 caracteres como minimo")
    private String nombre;
    @NotBlank(message="El correo electrónico no puede estar en blanco")
    @Email(message="El email debe ser valido")
    @Column(unique=true)
    private String email;
    @NotBlank(message="El telefono no puede estar en blanco")
    private String telefono;

    @OneToMany(targetEntity = Reserva.class, cascade = CascadeType.ALL, mappedBy = "cliente")
    private List<Reserva> reservas = new ArrayList<>();
}
