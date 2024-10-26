package com.backend.integrador.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.backend.integrador.Models.Contacto;
import com.backend.integrador.Repository.ContactoRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/contacto")
@CrossOrigin(origins = "http://localhost:3000")
public class FormularioContactoController {

    @Autowired
    ContactoRepositorio contactoRepositorio;

    @PostMapping("/enviar")
    public ResponseEntity<String> enviarContacto(@RequestBody Contacto contacto) {
        contactoRepositorio.save(contacto);
        return new ResponseEntity<>("Formulario enviado con éxito", HttpStatus.CREATED);
    }
}
