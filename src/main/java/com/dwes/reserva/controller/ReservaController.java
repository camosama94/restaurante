package com.dwes.reserva.controller;

import com.dwes.reserva.DTO.DetallesReservaDTO;
import com.dwes.reserva.entity.Cliente;
import com.dwes.reserva.entity.Mesa;
import com.dwes.reserva.entity.Reserva;
import com.dwes.reserva.repository.ClienteRepository;
import com.dwes.reserva.repository.MesaRepository;
import com.dwes.reserva.repository.ReservaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @PostMapping("/reservas")
    public ResponseEntity<?> addReserva(@RequestBody @Valid Reserva reserva) {
        try {
            if (reserva.getHora().getMinute() != 0 || reserva.getHora().getSecond() != 0) {
                return ResponseEntity.status (HttpStatus.BAD_REQUEST).body("La hora debe ser exacta (ej: 13:00, 14:00, 15:00)");
            }else {
                Optional<Cliente> cliente = clienteRepository.findById(reserva.getCliente().getId());//.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
                Optional<Mesa> mesa = mesaRepository.findById(reserva.getMesa().getId());
                if(!cliente.isPresent()){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
                }

                if(!mesa.isPresent()){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mesa no encontrada");
                }
                reserva.setCliente(cliente.get());
                reserva.setMesa(mesa.get());
                reservaRepository.save(reserva);
                return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
            }
        }catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La mesa ya tiene una reserva asignada para ese día y hora");
        }
    }



    @GetMapping("/detalles_reserva/{fecha}")
    public ResponseEntity<List<DetallesReservaDTO>> getDetallesReserva(@PathVariable LocalDate fecha) {
        List<DetallesReservaDTO> reservaDTO = new ArrayList<>();

        reservaRepository.findReservaByFecha(fecha).forEach(reserva -> {
            reservaDTO.add(
                    DetallesReservaDTO.builder()
                            .nombre(reserva.getCliente().getNombre())
                            .email(reserva.getCliente().getEmail())
                            .fecha(reserva.getFecha())
                            .hora(reserva.getHora())
                            .numeroMesa(reserva.getMesa().getNumero())
                            .descripcion(reserva.getMesa().getDescripcion())
                            .numeroPersonas(reserva.getNumeroPersonas())
                            .build()
            );
        });
        return ResponseEntity.ok(reservaDTO);
    }

    @DeleteMapping("/reservas/{id}")
    public ResponseEntity<?> deleteReserva(@PathVariable Long id) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reservaRepository.delete(reserva);
                    return ResponseEntity.noContent().build();
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada"));
    }
}
