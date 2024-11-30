package com.backend.integrador.Controller;

import com.backend.integrador.Exceptions.CorreoElectronicoYaExiste;
import com.backend.integrador.Models.Usuario;
import com.backend.integrador.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrar")
    public ResponseEntity<Object> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            if (usuario.getNombre() != null) {
                usuario.setNombre(Jsoup.clean(usuario.getNombre(), Safelist.basic()));
            }

            if (usuario.getCorreo() != null) {
                usuario.setCorreo(Jsoup.clean(usuario.getCorreo(), Safelist.basic()));
            }

            if (usuario.getApellido() != null) {
                usuario.setApellido(Jsoup.clean(usuario.getApellido(), Safelist.basic()));
            }

            Usuario usuarioRegistrado = usuarioService.registrarUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRegistrado); // Aseguramos que el código sea
                                                                                      // 201
        } catch (CorreoElectronicoYaExiste ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo electrónico ya está registrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud");
        }
    }

}
