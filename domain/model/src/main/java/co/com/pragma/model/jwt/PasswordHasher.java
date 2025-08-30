package co.com.pragma.model.jwt;

public interface PasswordHasher {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String hash);
}