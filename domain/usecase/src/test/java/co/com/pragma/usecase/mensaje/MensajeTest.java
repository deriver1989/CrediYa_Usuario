package co.com.pragma.usecase.mensaje;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MensajeTest {

    @Test
    void testConstantes() {
        assertEquals("Usuario ya existe.", Mensaje.USUARIO_EXISTE);
        assertEquals("Credenciales inválidas.", Mensaje.CREDENCIALES_INVALIDAS);
        assertEquals("El correo electrónico ya se encuentra registrado.", Mensaje.CORREO_ELECTRONICO_REGISTRADO);
    }
}