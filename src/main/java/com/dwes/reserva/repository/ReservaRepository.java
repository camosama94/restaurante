package com.dwes.reserva.repository;

import com.dwes.reserva.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findReservaByFecha(LocalDate fecha);

    List<Reserva> findByFechaAndHora(LocalDate fecha, LocalTime hora);

    List<Reserva> findByCliente_Id(Long idCliente);
}
