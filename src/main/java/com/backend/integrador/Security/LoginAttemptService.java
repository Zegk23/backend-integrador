package com.backend.integrador.Security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LoginAttemptService {

    private final int MAX_ATTEMPT = 5;
    private final long LOCK_TIME = TimeUnit.MINUTES.toMillis(15); // 15 minutos de bloqueo
    private final ConcurrentHashMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lockTimeCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
        lockTimeCache.remove(key);
        log.info("Usuario con clave '{}' inició sesión exitosamente. Los intentos de bloqueo fueron restablecidos.", key);
    }

    public void loginFailed(String key) {
        int attempts = attemptsCache.getOrDefault(key, 0) + 1;
        attemptsCache.put(key, attempts);

        if (attempts >= MAX_ATTEMPT) {
            lockTimeCache.put(key, System.currentTimeMillis());
            log.warn("Usuario con clave '{}' ha sido bloqueado tras {} intentos fallidos. Bloqueo activo por 15 minutos.", key, MAX_ATTEMPT);
        } else {
            int remainingAttempts = MAX_ATTEMPT - attempts;
            log.info("Intento fallido de inicio de sesión para la clave '{}'. Intentos restantes: {}", key, remainingAttempts);
        }
    }

    public boolean isBlocked(String key) {
        if (!lockTimeCache.containsKey(key)) {
            return false;
        }

        long lockTime = lockTimeCache.get(key);
        if (System.currentTimeMillis() - lockTime > LOCK_TIME) {
            lockTimeCache.remove(key);
            attemptsCache.remove(key);
            log.info("El bloqueo para la clave '{}' ha expirado. Se restableció el acceso.", key);
            return false;
        }

        long remainingTime = LOCK_TIME - (System.currentTimeMillis() - lockTime);
        log.info("Usuario con clave '{}' está bloqueado. Tiempo restante de bloqueo: {} segundos.", key, remainingTime / 1000);
        return true;
    }

    public int getRemainingAttempts(String key) {
        return MAX_ATTEMPT - attemptsCache.getOrDefault(key, 0);
    }
}
