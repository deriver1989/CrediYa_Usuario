package co.com.pragma.api.handler;

import co.com.pragma.api.request.UsuarioRequest;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.usecase.usuario.UsuarioUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Any;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertTrue;

class HandlerAutenticacionTest {

    private UsuarioRepository usuarioRepository;
    private UsuarioUseCase usuarioUseCase;
    private Validator validator;
    private WebTestClient webTestClient;
    private HandlerAutenticacion handler;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        usuarioUseCase = new UsuarioUseCase(usuarioRepository);
        validator = mock(Validator.class);

        handler = new HandlerAutenticacion(usuarioUseCase, validator);

        // Router solo para pruebas
        webTestClient = WebTestClient.bindToRouterFunction(
                RouterFunctions.route()
                        .GET("/hola", handler::holaMundo)
                        .POST("/usuarios", handler::guardarUsuarioNuevo)
                        .build()
        ).build();
    }

    @Test
    void testHolaMundo() {
        webTestClient.get()
                .uri("/hola")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hola Mundo");
    }

    @Test
    void testGuardarUsuarioExitoso() {
        UsuarioRequest request = UsuarioRequest.builder()
                .nombres("Juan")
                .apellidos("Pérez")
                .correo_electronico("juan.perez@example.com")
                .salario_base(1200000.0)
                .fecha_nacimiento(LocalDate.of(1990, 5, 10))
                .direccion("Calle 123")
                .telefono("3001234567")
                .build();

        Usuario usuarioGuardado = new Usuario(
                "Juan", "Pérez",
                request.getFecha_nacimiento(),
                "Calle 123",
                "3001234567",
                "juan.perez@example.com",
                1200000.0
        );

        when(usuarioRepository.existeCorreoElectronico("juan.perez@example.com"))
                .thenReturn(Mono.just(false));
        when(usuarioRepository.saveUsuario(any()))
                .thenReturn(Mono.just(usuarioGuardado));

        Usuario usuarioResponse = webTestClient.post()
                .uri("/usuarios")
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Usuario.class)
                .returnResult()
                .getResponseBody();

        assertThat(usuarioResponse.getCorreo_electronico())
                .isEqualTo("juan.perez@example.com");
    }

    @Test
    void testGuardarUsuarioConCorreoYaRegistrado() {
        UsuarioRequest request = UsuarioRequest.builder()
                .nombres("Ana")
                .apellidos("Ramírez")
                .correo_electronico("ana.ramirez@example.com")
                .salario_base(900000.0)
                .build();

        when(usuarioRepository.existeCorreoElectronico("ana.ramirez@example.com"))
                .thenReturn(Mono.just(true)); // Simula correo ya registrado

        webTestClient.post()
                .uri("/usuarios")
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Error al guardar el usuario")
                .jsonPath("$.detalle").isEqualTo("El correo electrónico ya se encuentra registrado.");
    }


    @Test
    void testValidacionConMultiplesErrores() {

        UsuarioRequest requestInvalido = UsuarioRequest.builder().build();
        ServerRequest srequest = mock(ServerRequest.class);

        when(srequest.bodyToMono(UsuarioRequest.class)).thenReturn(Mono.just(requestInvalido));
        doAnswer(invocationOnMock -> {
            BeanPropertyBindingResult errores = invocationOnMock.getArgument(1);
            errores.rejectValue("nombres", "errores.nombres", "prueba");
            return null;
        }).when(validator).validate(eq(requestInvalido), any());

        Mono<ServerResponse> resul = handler.guardarUsuarioNuevo(srequest);

        ServerResponse response = resul.block();

        assertEquals(400, response.statusCode().value());

    }
}
