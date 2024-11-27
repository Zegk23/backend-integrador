package com.backend.integrador.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Manejo de excepción para correo electrónico ya existente
    @ExceptionHandler(CorreoElectronicoYaExiste.class)
    public ResponseEntity<String> handleCorreoElectronicoYaExiste(CorreoElectronicoYaExiste e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // Manejo de excepción para stock insuficiente
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<String> handleStockInsuficienteException(StockInsuficienteException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // Manejo de excepción para Direccion Duplicada, es decir direccion ya antes registrada xd
    @ExceptionHandler(DireccionDuplicadaException.class)
    public ResponseEntity<String> handleDireccionDuplicadaException(DireccionDuplicadaException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

}
