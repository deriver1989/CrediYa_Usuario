package co.com.pragma.api.router;

import co.com.pragma.api.handler.HandlerAutenticacion;
import co.com.pragma.api.request.UsuarioRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
public class RouterUsuario {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios/guardar-usuario",
                    method = POST,
                    beanClass = HandlerAutenticacion.class,
                    beanMethod = "guardarUsuarioNuevo",
                    operation = @Operation(
                            operationId = "guardarUsuarioNuevo",
                            summary = "Guardar un nuevo usuario",
                            description = "Crea un nuevo usuario en el sistema",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos del usuario a guardar",
                                    content = @Content(
                                            schema = @Schema(implementation = UsuarioRequest.class),
                                            examples = @ExampleObject(
                                                    value = "{ \"nombres\": \"Juan\", \"apellidos\": \"Pérez\", \"fecha_nacimiento\": \"1990-01-01\", \"direccion\": \"Calle 123\", \"telefono\": \"3001234567\", \"correo_electronico\": \"juan.perez@example.com\", \"salario_base\": 2500000 }"
                                            )
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Usuario creado exitosamente",
                                            content = @Content(schema = @Schema(implementation = UsuarioRequest.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación en la solicitud"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/prueba",
                    method = GET,
                    beanClass = HandlerAutenticacion.class,
                    beanMethod = "holaMundo",
                    operation = @Operation(
                            operationId = "holaMundo",
                            summary = "Prueba de servicio",
                            description = "Devuelve un saludo de prueba",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Texto devuelto exitosamente",
                                            content = @Content(schema = @Schema(implementation = String.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(HandlerAutenticacion handler) {
        return route(POST("/api/v1/usuarios/guardar-usuario"), handler::guardarUsuarioNuevo)
                .and(route(GET("/api/v1/usuarios/prueba"), handler::holaMundo));
    }

}
