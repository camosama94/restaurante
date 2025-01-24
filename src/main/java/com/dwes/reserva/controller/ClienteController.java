package com.dwes.reserva.controller;

import com.dwes.reserva.entity.Cliente;
import com.dwes.reserva.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClienteController {

        @Autowired
        private ClienteRepository clienteRepository;

        @GetMapping("/Clientes")
        public ResponseEntity<List<Cliente>> getAllClientes() {
                List<Cliente> clientes = clienteRepository.findAll();
                return ResponseEntity.ok(clientes);
        }

        @PostMapping("/Clientes")
        public ResponseEntity<?> guardarCliente(@RequestBody @Valid Cliente cliente) {
                try {
                        Cliente clienteSalvo = clienteRepository.save(cliente);
                        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
                }catch (DataIntegrityViolationException e){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo electrónico ya esta registrado");
                }

        }

        @PutMapping("/Clientes/{id}")
        public ResponseEntity<Cliente> editarCliente(@PathVariable Long id, @RequestBody @Valid Cliente cliente) {
                return clienteRepository.findById(id)
                        .map(cliente1 ->{
                           cliente1.setNombre(cliente.getNombre());
                           cliente1.setEmail(cliente.getEmail());
                           cliente1.setTelefono(cliente.getTelefono());
                           clienteRepository.save(cliente1);
                           return ResponseEntity.ok(cliente1);
                        }).orElse(ResponseEntity.notFound().build());
        }

        @DeleteMapping("/Clientes/{id}")
        public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
                return clienteRepository.findById(id)
                        .map(cliente ->{
                                clienteRepository.delete(cliente);
                                return ResponseEntity.noContent().build();
                        }).orElse(ResponseEntity.notFound().build());
        }
}
