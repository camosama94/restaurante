package com.dwes.reserva.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="reservas", uniqueConstraints = {@UniqueConstraint(columnNames = {"id_mesa", "fecha", "hora"})})
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message="La reserva debe tener una fecha")
    @FutureOrPresent(message = "La reserva no puede ser para una fecha pasada")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate fecha;

    @NotNull(message = "La reserva debe tener una hora")
    private LocalTime hora;

    @ManyToOne(targetEntity = Cliente.class)
    @JoinColumn(name="id_cliente")
    @NotNull(message="La reserve debe pertenecer a un cliente")
    private Cliente cliente;

    @ManyToOne(targetEntity = Mesa.class)
    @JoinColumn(name="id_mesa")
    @NotNull(message="La reserva debe tener una mesa asignada")

    private Mesa mesa;

    @NotNull(message="Debe existir un numero de personas")
    private int numeroPersonas;

}
