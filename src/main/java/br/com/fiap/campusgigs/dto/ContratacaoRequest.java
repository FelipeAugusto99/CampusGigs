package br.com.fiap.campusgigs.dto;

import jakarta.validation.constraints.NotNull;

public record ContratacaoRequest(

        @NotNull(message = "Serviço é obrigatório")
        Long servicoId

) {
}