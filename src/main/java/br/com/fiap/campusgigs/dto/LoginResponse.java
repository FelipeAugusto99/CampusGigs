package br.com.fiap.campusgigs.dto;

public record LoginResponse(
        Long id,
        String nome,
        String email,
        String role,
        String mensagem
) {
}