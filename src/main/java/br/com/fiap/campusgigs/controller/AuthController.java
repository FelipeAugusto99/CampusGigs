package br.com.fiap.campusgigs.controller;

import br.com.fiap.campusgigs.dto.CadastroRequest;
import br.com.fiap.campusgigs.dto.LoginRequest;
import br.com.fiap.campusgigs.dto.LoginResponse;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Map<String, Object>> cadastrar(
            @Valid @RequestBody CadastroRequest request
    ) {
        Usuario usuario = authService.cadastrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", usuario.getId(),
                        "nome", usuario.getNome(),
                        "email", usuario.getEmail(),
                        "role", usuario.getRole().name(),
                        "mensagem", "Usuário cadastrado com sucesso"
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request
    ) {
        try {
            LoginResponse response = authService.autenticar(request);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}