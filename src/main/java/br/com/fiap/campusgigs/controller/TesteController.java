package br.com.fiap.campusgigs.controller;

import br.com.fiap.campusgigs.model.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/teste")
public class TesteController {

    @GetMapping("/protegido")
    public Map<String, Object> protegido(
            Authentication authentication
    ) {

        Usuario usuario =
                (Usuario) authentication.getPrincipal();

        return Map.of(
                "mensagem", "Você acessou um endpoint protegido",
                "usuario", usuario.getNome(),
                "email", usuario.getEmail(),
                "role", usuario.getRole().name()
        );
    }
}