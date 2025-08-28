package co.com.pragma.api.config;

import co.com.pragma.api.ApiApplication;
import co.com.pragma.api.handler.HandlerAutenticacion;
import co.com.pragma.api.request.UsuarioRequest;
import co.com.pragma.api.router.RouterUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;


@WebFluxTest(controllers = RouterUsuario.class, excludeAutoConfiguration = CorsConfig.class)
@Import(HandlerAutenticacion.class) // Importas solo lo que necesitas
@ContextConfiguration(classes = {RouterUsuario.class, HandlerAutenticacion.class})
class RouterUsuarioTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private HandlerAutenticacion handler; // Se inyecta el mock en RouterUsuario

    private UsuarioRequest usuarioRequest;

    @BeforeEach
    void setUp() {
        usuarioRequest = UsuarioRequest.builder()
                .nombres("Juan")
                .apellidos("Pérez")
                .fecha_nacimiento(LocalDate.of(1990, 1, 1))
                .direccion("Calle 123")
                .telefono("3001234567")
                .correo_electronico("juan.perez@example.com")
                .salario_base(2500000.0)
                .build();
    }

    @Test
    void testGuardarUsuarioNuevo() {
        // Simulamos la respuesta del handler
        Mockito.when(handler.guardarUsuarioNuevo(any()))
                .thenReturn(ServerResponse.ok().bodyValue(usuarioRequest));

        webTestClient.post()
                .uri("/api/v1/usuarios/guardar-usuario")
                .bodyValue(usuarioRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.nombres").isEqualTo("Juan")
                .jsonPath("$.correo_electronico").isEqualTo("juan.perez@example.com");
    }

    @Test
    void testHolaMundo() {
        Mockito.when(handler.holaMundo(any()))
                .thenReturn(ServerResponse.ok().bodyValue("Hola Mundo"));

        webTestClient.get()
                .uri("/api/v1/usuarios/prueba")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Hola Mundo");
    }
}
