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
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@RestController
@RequestMapping("/api/contacto")
public class FormularioContactoController {

    @Autowired
    ContactoRepositorio contactoRepositorio;

    @PostMapping("/enviar")
    public ResponseEntity<String> enviarContacto(@RequestBody Contacto contacto) {
        if (contacto.getNombre() == null || contacto.getCorreo() == null || contacto.getMensaje() == null) {
            return new ResponseEntity<>("Los campos no pueden ser nulos", HttpStatus.BAD_REQUEST);
        }

        contacto.setNombre(Jsoup.clean(contacto.getNombre(), Safelist.basic()));
        contacto.setCorreo(Jsoup.clean(contacto.getCorreo(), Safelist.basic()));
        contacto.setMensaje(Jsoup.clean(contacto.getMensaje(), Safelist.basic()));

        contactoRepositorio.save(contacto);
        return new ResponseEntity<>("Formulario enviado con éxito", HttpStatus.CREATED);
    }

}
