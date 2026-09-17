package br.com.fiap.campusgigs.client;

import br.com.fiap.campusgigs.dto.ViaCepResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/ws")
public interface ViaCepClient {

    @GetExchange("/{cep}/json")
    ViaCepResponse buscarCep(
            @PathVariable("cep") String cep
    );
}