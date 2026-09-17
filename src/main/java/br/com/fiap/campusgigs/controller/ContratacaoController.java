package br.com.fiap.campusgigs.controller;

import br.com.fiap.campusgigs.dto.ContratacaoRequest;
import br.com.fiap.campusgigs.dto.ContratacaoResponse;
import br.com.fiap.campusgigs.dto.SituacaoContratacaoRequest;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.service.ContratacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contratacoes")
public class ContratacaoController {

    private final ContratacaoService contratacaoService;

    public ContratacaoController(
            ContratacaoService contratacaoService
    ) {
        this.contratacaoService = contratacaoService;
    }

    @PostMapping
    public ResponseEntity<ContratacaoResponse> solicitar(
            @Valid @RequestBody ContratacaoRequest request,
            Authentication authentication
    ) {
        Usuario usuario =
                (Usuario) authentication.getPrincipal();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        contratacaoService.solicitar(
                                request,
                                usuario
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<ContratacaoResponse>> listar(
            Authentication authentication
    ) {
        Usuario usuario =
                (Usuario) authentication.getPrincipal();

        return ResponseEntity.ok(
                contratacaoService.listar(usuario)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratacaoResponse> buscarPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Usuario usuario =
                (Usuario) authentication.getPrincipal();

        return ResponseEntity.ok(
                contratacaoService.buscarPorId(
                        id,
                        usuario
                )
        );
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<ContratacaoResponse> alterarSituacao(
            @PathVariable Long id,
            @Valid @RequestBody SituacaoContratacaoRequest request,
            Authentication authentication
    ) {
        Usuario usuario =
                (Usuario) authentication.getPrincipal();

        return ResponseEntity.ok(
                contratacaoService.alterarSituacao(
                        id,
                        request.situacao(),
                        usuario
                )
        );
    }
}