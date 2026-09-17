package br.com.fiap.campusgigs.controller;

import br.com.fiap.campusgigs.dto.ServicoRequest;
import br.com.fiap.campusgigs.dto.ServicoResponse;
import br.com.fiap.campusgigs.dto.SituacaoServicoRequest;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @PostMapping
    public ResponseEntity<ServicoResponse> criar(
            @Valid @RequestBody ServicoRequest request,
            Authentication authentication
    ) {
        Usuario usuario =
                (Usuario) authentication.getPrincipal();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        servicoService.criar(
                                request,
                                usuario
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<ServicoResponse>> listar() {
        return ResponseEntity.ok(
                servicoService.listar()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponse> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                servicoService.buscarPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ServicoRequest request,
            Authentication authentication
    ) {
        Usuario usuario =
                (Usuario) authentication.getPrincipal();

        return ResponseEntity.ok(
                servicoService.atualizar(
                        id,
                        request,
                        usuario
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Usuario usuario =
                (Usuario) authentication.getPrincipal();

        servicoService.excluir(
                id,
                usuario
        );

        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<ServicoResponse> alterarSituacao(
            @PathVariable Long id,
            @Valid @RequestBody SituacaoServicoRequest request,
            Authentication authentication
    ) {
        Usuario usuario =
                (Usuario) authentication.getPrincipal();

        return ResponseEntity.ok(
                servicoService.alterarSituacao(
                        id,
                        request.situacao(),
                        usuario
                )
        );
    }
}