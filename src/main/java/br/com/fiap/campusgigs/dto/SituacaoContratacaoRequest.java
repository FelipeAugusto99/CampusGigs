package br.com.fiap.campusgigs.dto;

import br.com.fiap.campusgigs.model.SituacaoContratacao;
import jakarta.validation.constraints.NotNull;

public record SituacaoContratacaoRequest(

        @NotNull(message = "Situação é obrigatória")
        SituacaoContratacao situacao

) {
}