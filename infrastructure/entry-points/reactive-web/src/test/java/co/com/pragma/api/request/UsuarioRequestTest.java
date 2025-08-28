package co.com.pragma.api.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void probarGettersYSetters() {
        UsuarioRequest usuario = new UsuarioRequest();

        usuario.setNombres("Mariana");
        usuario.setApellidos("López");
        usuario.setFecha_nacimiento(LocalDate.of(1993, 7, 9));
        usuario.setDireccion("Transversal 23 #101-45, Bogotá");
        usuario.setTelefono("3106547890");
        usuario.setCorreo_electronico("mariana.lopez@example.com");
        usuario.setSalario_base(4280000.50);

        assertThat(usuario.getNombres()).isEqualTo("Mariana");
        assertThat(usuario.getApellidos()).isEqualTo("López");
        assertThat(usuario.getFecha_nacimiento()).isEqualTo(LocalDate.of(1993, 7, 9));
        assertThat(usuario.getDireccion()).isEqualTo("Transversal 23 #101-45, Bogotá");
        assertThat(usuario.getTelefono()).isEqualTo("3106547890");
        assertThat(usuario.getCorreo_electronico()).isEqualTo("mariana.lopez@example.com");
        assertThat(usuario.getSalario_base()).isEqualTo(4280000.50);
    }

    @Test
    void probarBuilderDeUsuario() {
        UsuarioRequest usuario = UsuarioRequest.builder()
                .nombres("Mariana")
                .apellidos("López")
                .correo_electronico("mariana.lopez@example.com")
                .salario_base(4200000.0)
                .build();

        assertThat(usuario.getNombres()).isEqualTo("Mariana");
        assertThat(usuario.getApellidos()).isEqualTo("López");
        assertThat(usuario.getCorreo_electronico()).isEqualTo("mariana.lopez@example.com");
    }


    @Test
    void pruebaValidacionesRequest() {
        UsuarioRequest usuario = UsuarioRequest.builder()
                .nombres("")
                .apellidos(null)
                .correo_electronico("correo-no-valido")
                .salario_base(20000000.0)
                .build();

        Mono<Set<ConstraintViolation<UsuarioRequest>>> result =
                Mono.fromCallable(() -> validator.validate(usuario));

        StepVerifier.create(result)
                .assertNext(violations -> {
                    assertThat(violations).isNotEmpty();
                    assertThat(violations).anyMatch(v -> v.getMessage().contains("no puede estar vacío"));
                    assertThat(violations).anyMatch(v -> v.getMessage().contains("es obligatorio"));
                    assertThat(violations).anyMatch(v -> v.getMessage().contains("no es válido"));
                    assertThat(violations).anyMatch(v -> v.getMessage().contains("no puede superar"));
                })
                .verifyComplete();
    }
}