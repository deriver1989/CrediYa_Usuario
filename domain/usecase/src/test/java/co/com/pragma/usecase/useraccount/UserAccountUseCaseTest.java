package co.com.pragma.usecase.useraccount;

import co.com.pragma.model.jwt.PasswordHasher;

import co.com.pragma.model.useraccount.UserAccount;
import co.com.pragma.model.useraccount.gateways.UserAccountRepository;
import co.com.pragma.usecase.mensaje.Mensaje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import java.util.Set;


import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class UserAccountUseCaseTest {

    private UserAccountRepository gateway;
    private PasswordHasher passwordHasher;
    private UserAccountUseCase userAccountUseCase;

    private String username;
    private String pass;


    @BeforeEach
    void setUp() {
        gateway = Mockito.mock(UserAccountRepository.class);
        passwordHasher = Mockito.mock(PasswordHasher.class);
        userAccountUseCase = new UserAccountUseCase(gateway, passwordHasher);
        username = "prueba@prueba.com";
        pass="clave";
    }

    @Test
    void autenticar() {
        UserAccount user = new UserAccount(
                "c7dba732-4a65-4c96-9f03-c2607bafe2c8",
                "prueba@prueba.com",
                "clave",
                Set.of("USER", "ADMIN")
        );
        when(gateway.findByUsername(anyString())).thenReturn(Mono.just(user));

        when(passwordHasher.matches(anyString(), anyString())).thenReturn(true);

        StepVerifier.create(userAccountUseCase.authenticate(username, pass))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void authenticate_ok() {
        // Arrange
        String username = "juan";
        String rawPassword = "1234";
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPasswordHash("hashed");

        when(gateway.findByUsername(anyString())).thenReturn(Mono.just(user));
        when(passwordHasher.matches(anyString(), anyString())).thenReturn(true);

        // Act
        UserAccount result = userAccountUseCase.authenticate(username, rawPassword).block();

        assertNotNull(result);
        assertEquals(username, result.getUsername());

    }

    @Test
    void authenticate_userNotFound() {
        String username = "juan";
        String rawPassword = "1234";

        when(gateway.findByUsername(username)).thenReturn(Mono.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                userAccountUseCase.authenticate(username, rawPassword).block()
        );

        assertEquals(Mensaje.CREDENCIALES_INVALIDAS, ex.getMessage());
    }

    @Test
    void authenticate_invalidPassword() {
        String username = "juan";
        String rawPassword = "wrong";
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPasswordHash("hashed");

        when(gateway.findByUsername(anyString())).thenReturn(Mono.just(user));
        when(passwordHasher.matches(anyString(), anyString())).thenReturn(false);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                userAccountUseCase.authenticate(username, rawPassword).block()
        );

        assertEquals(Mensaje.CREDENCIALES_INVALIDAS, ex.getMessage());
    }


    @Test
    void register_usuario_existe() {
        UserAccount user = new UserAccount(
                "c7dba732-4a65-4c96-9f03-c2607bafe2c8",
                "prueba@prueba.com",
                "clave",
                Set.of("USER", "ADMIN")
        );
        when(gateway.existsByUsername(anyString())).thenReturn(Mono.just(true));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                userAccountUseCase.register(user.getUsername(), user.getPasswordHash(), user.getRoles()).block()
        );

        assertEquals(Mensaje.USUARIO_EXISTE, ex.getMessage());
    }

    @Test
    void register_NO_usuario_existe() {
        // Arrange
        UserAccount user = new UserAccount(
                "c7dba732-4a65-4c96-9f03-c2607bafe2c8",
                "prueba@prueba.com",
                "clave",
                Set.of("USER", "ADMIN")
        );

        when(gateway.existsByUsername(anyString())).thenReturn(Mono.just(false));
        when(gateway.save(ArgumentMatchers.any(UserAccount.class))).thenReturn(Mono.just(user));

        UserAccount result = userAccountUseCase.register(user.getUsername(), user.getPasswordHash(), user.getRoles()).block();

        assertNotNull(result);
        assertEquals(username, result.getUsername());

    }

    @Test
    void register_NO_usuario_existe_rolenull() {
        // Arrange
        UserAccount user = new UserAccount(
                "c7dba732-4a65-4c96-9f03-c2607bafe2c8",
                "prueba@prueba.com",
                "clave",
                null
        );

        when(gateway.existsByUsername(anyString())).thenReturn(Mono.just(false));
        when(gateway.save(ArgumentMatchers.any(UserAccount.class))).thenReturn(Mono.just(user));

        UserAccount result = userAccountUseCase.register(user.getUsername(), user.getPasswordHash(), user.getRoles()).block();

        assertNotNull(result);
        assertEquals(username, result.getUsername());

    }
/*
    @Test
    void register_newUser_shouldSaveUser() {
        // Arrange
        String username = "maria";
        String rawPassword = "1234";
        String hashedPassword = "hashed1234";

        when(gateway.existsByUsername(username)).thenReturn(Mono.just(false));
        when(passwordHasher.hash(rawPassword)).thenReturn(hashedPassword);

        UserAccount savedUser = new UserAccount("1", username, hashedPassword, Set.of("ADMIN"));
        when(gateway.save(ArgumentMatchers.any(UserAccount.class))).thenReturn(Mono.just(savedUser));

        // Act
        Mono<UserAccount> result = userAccountUseCase.register(username, rawPassword, Set.of("ADMIN"));

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(user ->
                        user.getId().equals("1") &&
                                user.getUsername().equals(username) &&
                                user.getPasswordHash().equals(hashedPassword) &&
                                user.getRoles().contains("ADMIN")
                )
                .verifyComplete();

        verify(gateway, times(1)).save(ArgumentMatchers.any(UserAccount.class));
    }*/
}