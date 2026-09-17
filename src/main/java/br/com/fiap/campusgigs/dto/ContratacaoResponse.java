package br.com.fiap.campusgigs.dto;

import br.com.fiap.campusgigs.model.Contratacao;

import java.math.BigDecimal;

public record ContratacaoResponse(

        Long id,

        Long servicoId,
        String servicoTitulo,
        BigDecimal servicoPreco,

        Long contratanteId,
        String contratanteNome,

        Long prestadorId,
        String prestadorNome,

        String situacao

) {

    public ContratacaoResponse(Contratacao contratacao) {
        this(
                contratacao.getId(),

                contratacao.getServico().getId(),
                contratacao.getServico().getTitulo(),
                contratacao.getServico().getPreco(),

                contratacao.getContratante().getId(),
                contratacao.getContratante().getNome(),

                contratacao.getServico().getPrestador().getId(),
                contratacao.getServico().getPrestador().getNome(),

                contratacao.getSituacao().name()
        );
    }
}