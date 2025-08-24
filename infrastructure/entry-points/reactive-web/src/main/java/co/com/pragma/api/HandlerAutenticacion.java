package co.com.pragma.api;

import co.com.pragma.api.request.UsuarioRequest;
import co.com.pragma.usecase.usuario.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class HandlerAutenticacion {
//private  final UseCase useCase;
//private  final UseCase2 useCase2;

    private final UsuarioUseCase usuarioUseCase;

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> holaMundo(ServerRequest serverRequest) {
        // useCase2.logic();
        return ServerResponse.ok().bodyValue("Hola Mundo");
    }

    public Mono<ServerResponse> guardarUsuarioNuevo(ServerRequest serverRequest) {
        // useCase.logic();
        Mono<UsuarioRequest> userRequestMono = serverRequest.bodyToMono(UsuarioRequest.class);

        return userRequestMono
                .flatMap(userReq ->
                        usuarioUseCase.guardarUsuario(userReq.getNombres(),
                                        userReq.getApellidos(),
                                        userReq.getFecha_nacimiento(),
                                        userReq.getDireccion(),
                                        userReq.getTelefono(),
                                        userReq.getCorreo_electronico(),
                                        userReq.getSalario_base())
                                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                );
    }
}
