package com.dwes.reserva.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class DetallesReservaDTO {
    private String nombre;
    private String email;
    private LocalDate fecha;
    private LocalTime hora;
    private Long numeroMesa;
    private String descripcion;
    private int numeroPersonas;
}
