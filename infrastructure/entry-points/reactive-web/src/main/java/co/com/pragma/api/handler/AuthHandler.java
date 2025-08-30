package co.com.pragma.api.handler;


import co.com.pragma.api.security.JwtService;
import co.com.pragma.usecase.useraccount.UserAccountUseCase;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@Component
public class AuthHandler {

    private final UserAccountUseCase authService;
    private final JwtService jwtService;

    public AuthHandler(UserAccountUseCase authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    public record SignUpDTO(String username, String password) {}
    public record LoginDTO(String username, String password) {}

    public Mono<ServerResponse> signUp(ServerRequest request) {
        return request.bodyToMono(SignUpDTO.class)
                .flatMap(dto -> authService.register(dto.username(), dto.password(), Set.of("USER")))
                .flatMap(u -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("id", u.getId(), "username", u.getUsername(), "roles", u.getRoles())));
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginDTO.class)
                .flatMap(dto -> authService.authenticate(dto.username(), dto.password()))
                .map(u -> Map.of(
                        "access_token", jwtService.generate(u.getUsername(), u.getRoles()),
                        "token_type", "Bearer",
                        "expires_in_minutes", 120
                ))
                .flatMap(body -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(body));
    }

    public Mono<ServerResponse> me(ServerRequest request) {
        return request.principal()
                .cast(Jwt.class) // el principal será Jwt cuando el token esté validado
                .flatMap(jwt -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of(
                                "sub", jwt.getSubject(),
                                "roles", jwt.getClaimAsStringList("roles"),
                                "exp", jwt.getExpiresAt()
                        )));
    }
}
