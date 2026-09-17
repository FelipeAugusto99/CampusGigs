package br.com.fiap.campusgigs.service;

import br.com.fiap.campusgigs.dto.ServicoRequest;
import br.com.fiap.campusgigs.dto.ServicoResponse;
import br.com.fiap.campusgigs.model.Role;
import br.com.fiap.campusgigs.model.Servico;
import br.com.fiap.campusgigs.model.SituacaoServico;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.repository.ServicoRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    public ServicoResponse criar(
            ServicoRequest request,
            Usuario usuario
    ) {
        Servico servico = new Servico();

        servico.setTitulo(request.titulo());
        servico.setDescricao(request.descricao());
        servico.setCategoria(request.categoria());
        servico.setPreco(request.preco());
        servico.setSituacao(SituacaoServico.ATIVO);
        servico.setPrestador(usuario);

        return new ServicoResponse(
                servicoRepository.save(servico)
        );
    }

    public List<ServicoResponse> listar() {
        return servicoRepository.findAll()
                .stream()
                .map(ServicoResponse::new)
                .toList();
    }

    public ServicoResponse buscarPorId(Long id) {
        return new ServicoResponse(
                buscarServico(id)
        );
    }

    public ServicoResponse atualizar(
            Long id,
            ServicoRequest request,
            Usuario usuario
    ) {
        Servico servico = buscarServico(id);

        verificarPermissao(servico, usuario);

        servico.setTitulo(request.titulo());
        servico.setDescricao(request.descricao());
        servico.setCategoria(request.categoria());
        servico.setPreco(request.preco());

        return new ServicoResponse(
                servicoRepository.save(servico)
        );
    }

    public void excluir(
            Long id,
            Usuario usuario
    ) {
        Servico servico = buscarServico(id);

        verificarPermissao(servico, usuario);

        servicoRepository.delete(servico);
    }

    public ServicoResponse alterarSituacao(
            Long id,
            SituacaoServico novaSituacao,
            Usuario usuario
    ) {
        Servico servico = buscarServico(id);

        verificarPermissao(servico, usuario);

        if (servico.getSituacao() == SituacaoServico.ENCERRADO) {
            throw new IllegalStateException(
                    "Um serviço encerrado não pode ter sua situação alterada"
            );
        }

        if (servico.getSituacao() == novaSituacao) {
            throw new IllegalStateException(
                    "O serviço já está na situação informada"
            );
        }

        servico.setSituacao(novaSituacao);

        return new ServicoResponse(
                servicoRepository.save(servico)
        );
    }

    private Servico buscarServico(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Serviço não encontrado"
                        )
                );
    }

    private void verificarPermissao(
            Servico servico,
            Usuario usuario
    ) {
        boolean admin =
                usuario.getRole() == Role.ADMIN;

        boolean proprietario =
                servico.getPrestador()
                        .getId()
                        .equals(usuario.getId());

        if (!admin && !proprietario) {
            throw new AccessDeniedException(
                    "Você não possui permissão para alterar este serviço"
            );
        }
    }
}