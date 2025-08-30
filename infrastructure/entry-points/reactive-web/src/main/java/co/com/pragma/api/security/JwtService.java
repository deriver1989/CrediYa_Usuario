package co.com.pragma.api.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Set;

@Component
public class JwtService {

    private final JwtEncoder encoder;
    private final ReactiveJwtDecoder decoder;
    private final long expirationMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secretHex,
            @Value("${app.jwt.expiration-minutes:120}") long expirationMinutes) {

        // Decodificar la clave desde HEX a bytes
        byte[] keyBytes = HexFormat.of().parseHex(secretHex);
        //final byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("El secreto JWT debe tener al menos 32 bytes para HS256");
        }

        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");

        // Encoder con HS256
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));

        // Decoder para validar tokens entrantes
        this.decoder = NimbusReactiveJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        this.expirationMinutes = expirationMinutes;
    }

    public String generate(String subject, Set<String> roles) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("pragma-api")
                .issuedAt(now)
                .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
                .subject(subject)
                .claim("roles", roles)
                .build();

        //return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return this.encoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).build(),  // 👈 fuerza HS256
                        claims
                )
        ).getTokenValue();
    }

    public ReactiveJwtDecoder jwtDecoder() {
        return this.decoder;
    }
}
