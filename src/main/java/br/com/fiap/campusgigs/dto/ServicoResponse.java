package br.com.fiap.campusgigs.dto;

import br.com.fiap.campusgigs.model.Servico;

import java.math.BigDecimal;

public record ServicoResponse(
        Long id,
        String titulo,
        String descricao,
        String categoria,
        BigDecimal preco,
        String situacao,
        Long prestadorId,
        String prestadorNome
) {

    public ServicoResponse(Servico servico) {
        this(
                servico.getId(),
                servico.getTitulo(),
                servico.getDescricao(),
                servico.getCategoria(),
                servico.getPreco(),
                servico.getSituacao().name(),
                servico.getPrestador().getId(),
                servico.getPrestador().getNome()
        );
    }
}