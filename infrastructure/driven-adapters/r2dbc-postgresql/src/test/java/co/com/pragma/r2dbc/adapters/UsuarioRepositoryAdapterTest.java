package co.com.pragma.r2dbc.adapters;

import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.r2dbc.entity.UsuarioEntity;
import co.com.pragma.r2dbc.repository.UsuarioEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioRepositoryAdapterTest {

    @Mock
    private UsuarioEntityRepository usuarioEntityRepository;

    @Mock
    private TransactionalOperator txOperator;

    @InjectMocks
    private UsuarioRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Simula que el operador transaccional devuelve el mismo Mono
        when(txOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void saveUsuario_debeGuardarYDevolverUsuario() {
        Usuario usuario = new Usuario("Juan", "Perez", null, "Calle 123", "3001234567", "juan@test.com", 2000.0);

        UsuarioEntity savedEntity = new UsuarioEntity(
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getFecha_nacimiento(),
                usuario.getDireccion(),
                usuario.getTelefono(),
                usuario.getCorreo_electronico(),
                usuario.getSalario_base()
        );

        when(usuarioEntityRepository.save(any(UsuarioEntity.class))).thenReturn(Mono.just(savedEntity));

        StepVerifier.create(adapter.saveUsuario(usuario))
                .expectNextMatches(u ->
                        u.getNombres().equals("Juan") &&
                                u.getApellidos().equals("Perez") &&
                                u.getCorreo_electronico().equals("juan@test.com"))
                .verifyComplete();

        verify(usuarioEntityRepository).save(any(UsuarioEntity.class));
    }

    @Test
    void existeCorreoElectronico_debeRetornarTrueCuandoExiste() {
        when(usuarioEntityRepository.existsByCorreoElectronico("correo@test.com")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existeCorreoElectronico("correo@test.com"))
                .expectNext(true)
                .verifyComplete();

        verify(usuarioEntityRepository).existsByCorreoElectronico("correo@test.com");
    }

    @Test
    void existeCorreoElectronico_debeRetornarFalseCuandoNoExiste() {
        when(usuarioEntityRepository.existsByCorreoElectronico("otro@test.com")).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.existeCorreoElectronico("otro@test.com"))
                .expectNext(false)
                .verifyComplete();

        verify(usuarioEntityRepository).existsByCorreoElectronico("otro@test.com");
    }
}
