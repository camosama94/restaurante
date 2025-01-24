package com.dwes.reserva.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="mesas")
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message="Debes introducir un número de mesa")
    @Column(unique=true)
    private Long numero;
    @NotBlank(message="La mesa debe tener una descripción")
    private String descripcion;

    @OneToMany(targetEntity = Reserva.class, cascade = CascadeType.ALL, mappedBy = "mesa")
    private List<Reserva> reservas = new ArrayList<>();
}
