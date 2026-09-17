package br.com.fiap.campusgigs.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServicoRequest(

        @NotBlank(message = "Título é obrigatório")
        @Size(max = 150)
        String titulo,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 500)
        String descricao,

        @NotBlank(message = "Categoria é obrigatória")
        @Size(max = 100)
        String categoria,

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
        BigDecimal preco
) {
}