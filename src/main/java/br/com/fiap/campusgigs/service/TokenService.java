package br.com.fiap.campusgigs.service;

import br.com.fiap.campusgigs.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private static final String SECRET =
            "campusgigs-chave-secreta-2026";

    private final Algorithm algorithm =
            Algorithm.HMAC256(SECRET);

    public String gerarToken(Usuario usuario) {

        Instant agora = Instant.now();

        return JWT.create()
                .withIssuer("CampusGigs")
                .withSubject(usuario.getEmail())
                .withClaim("role", usuario.getRole().name())
                .withIssuedAt(agora)
                .withExpiresAt(agora.plus(2, ChronoUnit.HOURS))
                .sign(algorithm);
    }

    public String validarToken(String token) {

        try {
            return JWT.require(algorithm)
                    .withIssuer("CampusGigs")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}