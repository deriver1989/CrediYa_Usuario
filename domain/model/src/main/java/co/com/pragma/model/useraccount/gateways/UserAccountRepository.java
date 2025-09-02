package co.com.pragma.model.useraccount.gateways;

import co.com.pragma.model.useraccount.UserAccount;
import reactor.core.publisher.Mono;

public interface UserAccountRepository {

    Mono<UserAccount> findByUsername(String username);
    Mono<UserAccount> save(UserAccount user);
    Mono<Boolean> existsByUsername(String username);
}
