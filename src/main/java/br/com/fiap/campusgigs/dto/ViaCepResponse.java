package br.com.fiap.campusgigs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(

        String cep,
        String localidade,
        String uf,
        Boolean erro

) {
}