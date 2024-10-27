package com.backend.integrador.Exceptions;

public class CorreoElectronicoYaExiste extends RuntimeException{
    public CorreoElectronicoYaExiste(String mensaje){
        super(mensaje);
    }
}
