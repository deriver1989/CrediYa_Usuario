package co.com.pragma.r2dbc.adapters;


import co.com.pragma.model.useraccount.UserAccount;
import co.com.pragma.model.useraccount.gateways.UserAccountRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.repository.UserEntityRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserAdapter implements UserAccountRepository {

    private final UserEntityRepository repo;

    public UserAdapter(UserEntityRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<UserAccount> findByUsername(String username) {
        return repo.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Mono<UserAccount> save(UserAccount user) {
        return repo.save(toEntity(user)).map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return repo.existsByUsername(username);
    }

    private UserAccount toDomain(UserEntity e) {
        Set<String> roles = e.getRoles() == null || e.getRoles().isBlank()
                ? Set.of()
                : Arrays.stream(e.getRoles().split(",")).map(String::trim).collect(Collectors.toSet());
        return new UserAccount(e.getId().toString(), e.getUsername(), e.getPasswordHash(), roles);
    }

    private UserEntity toEntity(UserAccount u) {
        UserEntity e = new UserEntity();
        e.setId(u.getId() == null ? null : UUID.fromString(u.getId()));
        e.setUsername(u.getUsername());
        e.setPasswordHash(u.getPasswordHash());
        e.setRoles(String.join(",", u.getRoles()));
        return e;
    }
}
