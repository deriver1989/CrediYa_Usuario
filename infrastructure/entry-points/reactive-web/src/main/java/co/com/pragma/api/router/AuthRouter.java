package co.com.pragma.api.router;

import co.com.pragma.api.handler.AuthHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AuthRouter {

    @Bean
    public RouterFunction<?> authRoutes(AuthHandler handler) {
        return route(POST("/auth/signup"), handler::signUp)
                .andRoute(POST("/auth/login"), handler::login)
                .andRoute(GET("/auth/me"), handler::me);
    }
}
