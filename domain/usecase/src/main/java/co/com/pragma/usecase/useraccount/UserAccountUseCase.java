package co.com.pragma.usecase.useraccount;

import co.com.pragma.model.jwt.PasswordHasher;
import co.com.pragma.model.useraccount.UserAccount;
import co.com.pragma.model.useraccount.gateways.UserAccountRepository;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public class UserAccountUseCase {

    private final UserAccountRepository gateway;
    //private final PasswordEncoder encoder;
    private final PasswordHasher passwordHasher;

    public UserAccountUseCase (UserAccountRepository gateway, PasswordHasher passwordHasher){
        this.gateway = gateway;
        this.passwordHasher = passwordHasher;;
    }

    public Mono<UserAccount> register(String username, String rawPassword, Set<String> roles) {
        return gateway.existsByUsername(username)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new IllegalArgumentException("Usuario ya existe"));
                    }
                    var user = new UserAccount(
                            null,//UUID.randomUUID().toString(),
                            username,
                            //encoder.encode(rawPassword),
                            passwordHasher.hash(rawPassword),
                            roles == null || roles.isEmpty() ? Set.of("CLIENTE") : roles
                    );
                    return gateway.save(user);
                });
    }

    public Mono<UserAccount> authenticate(String username, String rawPassword) {
        return gateway.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Credenciales inválidas")))
                //.flatMap(u -> encoder.matches(rawPassword, u.getPasswordHash())
                .flatMap(u -> passwordHasher.matches(rawPassword, u.getPasswordHash())
                        ? Mono.just(u)
                        : Mono.error(new IllegalArgumentException("Credenciales inválidas")));
    }
}
