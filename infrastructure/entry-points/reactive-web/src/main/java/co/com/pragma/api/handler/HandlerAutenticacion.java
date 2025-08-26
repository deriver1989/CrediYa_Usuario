package co.com.pragma.api.handler;

import co.com.pragma.api.request.UsuarioRequest;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.usecase.usuario.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class HandlerAutenticacion {

    private final UsuarioUseCase usuarioUseCase;
    private final Validator validator;

    public Mono<ServerResponse> holaMundo(ServerRequest serverRequest) {
        // useCase2.logic();
        return ServerResponse.ok().bodyValue("Hola Mundo");
    }

    public Mono<ServerResponse> guardarUsuarioNuevo(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(UsuarioRequest.class)
                .flatMap(userReq -> {
                    // Validar el objeto
                    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(userReq, UsuarioRequest.class.getName());
                    validator.validate(userReq, errors);

                    if (errors.hasErrors()) {
                        // devolver errores en formato JSON
                        return ServerResponse.badRequest().bodyValue(
                                errors.getFieldErrors().stream()
                                        .collect(java.util.stream.Collectors.toMap(
                                                e -> e.getField(),
                                                e -> e.getDefaultMessage()
                                        ))
                        );
                    }
                    // si pasa validación → usar el caso de uso
                    return  usuarioUseCase.guardarUsuario(mapToUsuario(userReq))
                            .flatMap(user -> ServerResponse.ok().bodyValue(user))
                            .onErrorResume(e ->{
                                    Map<String, Object> errorResponse = new HashMap<>();
                                    errorResponse.put("error", "Error al guardar el usuario");
                                    errorResponse.put("detalle", e.getMessage());
                                    return ServerResponse.badRequest().bodyValue(errorResponse);
                            });
                })
                .onErrorResume(e ->
                     ServerResponse.badRequest().bodyValue("Error al guardar el usuario: " + e.getMessage())
                );
    }

    private Usuario mapToUsuario(UsuarioRequest request) {
        return new Usuario(
                request.getNombres(),
                request.getApellidos(),
                request.getFecha_nacimiento(),
                request.getDireccion(),
                request.getTelefono(),
                request.getCorreo_electronico(),
                request.getSalario_base()
        );
    }
}
