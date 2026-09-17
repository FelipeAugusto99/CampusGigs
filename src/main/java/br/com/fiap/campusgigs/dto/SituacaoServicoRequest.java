package br.com.fiap.campusgigs.dto;

import br.com.fiap.campusgigs.model.SituacaoServico;
import jakarta.validation.constraints.NotNull;

public record SituacaoServicoRequest(

        @NotNull(message = "Situação é obrigatória")
        SituacaoServico situacao

) {
}