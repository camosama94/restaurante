package com.dwes.reserva.controller;

import com.dwes.reserva.entity.Mesa;
import com.dwes.reserva.repository.MesaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MesaController {

    @Autowired
    private MesaRepository mesaRepository;

    @GetMapping("/mesas")
    public ResponseEntity<List<Mesa>> mostrarMesas() {
        List<Mesa> mesas = mesaRepository.findAll();
        return ResponseEntity.ok(mesas);
    }

    @PostMapping("/mesas")
    public ResponseEntity<?> guardarMesa(@RequestBody @Valid Mesa mesa) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(mesaRepository.save(mesa));
        }catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La mesa ya existe");
        }

    }

    @PutMapping("/mesas/{id}")
    public ResponseEntity<Mesa> editarMesa(@PathVariable Long id, @RequestBody @Valid Mesa mesa) {
        return mesaRepository.findById(id)
                .map(mesa1 ->{
                    mesa1.setNumero(mesa.getNumero());
                    mesa1.setDescripcion(mesa.getDescripcion());
                    mesaRepository.save(mesa1);
                    return ResponseEntity.ok(mesa1);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/mesas/{id}")
    public ResponseEntity<?> eliminarMesa(@PathVariable Long id) {
        return mesaRepository.findById(id)
                .map(mesa ->{
                    mesaRepository.delete(mesa);
                    return ResponseEntity.noContent().build();
                }).orElse(ResponseEntity.notFound().build());
    }

}
