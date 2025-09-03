package co.com.pragma.usecase.useraccount;

import co.com.pragma.model.jwt.PasswordHasher;
import co.com.pragma.model.useraccount.UserAccount;
import co.com.pragma.model.useraccount.gateways.UserAccountRepository;
import co.com.pragma.usecase.mensaje.Mensaje;
import reactor.core.publisher.Mono;

import java.util.Set;

public class UserAccountUseCase {

    private final UserAccountRepository gateway;

    private final PasswordHasher passwordHasher;

    public UserAccountUseCase (UserAccountRepository gateway, PasswordHasher passwordHasher){
        this.gateway = gateway;
        this.passwordHasher = passwordHasher;;
    }

    public Mono<UserAccount> register(String username, String rawPassword, Set<String> roles) {
        return gateway.existsByUsername(username)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new IllegalArgumentException(Mensaje.USUARIO_EXISTE));
                    }
                    var user = new UserAccount(
                            null,
                            username,
                            passwordHasher.hash(rawPassword),
                            roles == null || roles.isEmpty() ? Set.of("CLIENTE") : roles
                    );
                    return gateway.save(user);
                });
    }

    public Mono<UserAccount> authenticate(String username, String rawPassword) {
        return gateway.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Mensaje.CREDENCIALES_INVALIDAS)))
                .flatMap(u -> passwordHasher.matches(rawPassword, u.getPasswordHash())
                        ? Mono.just(u)
                        : Mono.error(new IllegalArgumentException(Mensaje.CREDENCIALES_INVALIDAS)));
    }
}
