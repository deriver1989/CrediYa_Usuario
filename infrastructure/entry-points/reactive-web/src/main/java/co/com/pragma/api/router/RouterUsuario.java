package co.com.pragma.api.router;

import co.com.pragma.api.handler.HandlerAutenticacion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterUsuario {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(HandlerAutenticacion handler) {
        return route(GET("/api/v1/usuarios"), handler::listenGETUseCase)
                .andRoute(POST("/api/v1/usuarios/guardar-usuario"), handler::guardarUsuarioNuevo)
                .and(route(GET("/api/v1/usuarios/prueba"), handler::holaMundo));
    }
}
