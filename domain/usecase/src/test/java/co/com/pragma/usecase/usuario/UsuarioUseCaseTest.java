package co.com.pragma.usecase.usuario;

import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioUseCaseTest {

    private UsuarioRepository usuarioRepository;
    private UsuarioUseCase usuarioUseCase;

    @BeforeEach
    void setUp() {
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        usuarioUseCase = new UsuarioUseCase(usuarioRepository);
    }

    @Test
    void guardarUsuario_CuandoCorreoNoExiste_DeberiaGuardar() {
        // Arrange
        Usuario usuario = new Usuario(
                "Juan",
                "Pérez",
                LocalDate.of(1990, 1, 1),
                "Calle 123",
                "3001234567",
                "juan@test.com",
                1500.0,
                "9151203"
        );

        when(usuarioRepository.existeCorreoElectronico(usuario.getCorreo_electronico()))
                .thenReturn(Mono.just(false));

        when(usuarioRepository.saveUsuario(any(Usuario.class)))
                .thenReturn(Mono.just(usuario));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.guardarUsuario(usuario))
                .expectNext(usuario)
                .verifyComplete();

        verify(usuarioRepository).existeCorreoElectronico(usuario.getCorreo_electronico());
        verify(usuarioRepository).saveUsuario(usuario);
    }

    @Test
    void guardarUsuario_CuandoCorreoYaExiste_DeberiaRetornarError() {
        // Arrange
        Usuario usuario = new Usuario(
                "Ana",
                "Gómez",
                LocalDate.of(1992, 5, 10),
                "Carrera 45",
                "3109876543",
                "ana@test.com",
                2000.0,
                "9151202"
        );

        when(usuarioRepository.existeCorreoElectronico(usuario.getCorreo_electronico()))
                .thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(usuarioUseCase.guardarUsuario(usuario))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("El correo electrónico ya se encuentra registrado."))
                .verify();

        verify(usuarioRepository).existeCorreoElectronico(usuario.getCorreo_electronico());
        verify(usuarioRepository, never()).saveUsuario(any());
    }
}
