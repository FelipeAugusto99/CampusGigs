package br.com.fiap.campusgigs.service;

import br.com.fiap.campusgigs.dto.ContratacaoRequest;
import br.com.fiap.campusgigs.dto.ContratacaoResponse;
import br.com.fiap.campusgigs.model.*;
import br.com.fiap.campusgigs.repository.ContratacaoRepository;
import br.com.fiap.campusgigs.repository.ServicoRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContratacaoService {

    private final ContratacaoRepository contratacaoRepository;
    private final ServicoRepository servicoRepository;

    public ContratacaoService(
            ContratacaoRepository contratacaoRepository,
            ServicoRepository servicoRepository
    ) {
        this.contratacaoRepository = contratacaoRepository;
        this.servicoRepository = servicoRepository;
    }

    public ContratacaoResponse solicitar(
            ContratacaoRequest request,
            Usuario contratante
    ) {
        Servico servico = servicoRepository
                .findById(request.servicoId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Serviço não encontrado"
                        )
                );

        if (servico.getPrestador()
                .getId()
                .equals(contratante.getId())) {

            throw new IllegalStateException(
                    "Você não pode contratar o próprio serviço"
            );
        }

        if (servico.getSituacao() != SituacaoServico.ATIVO) {
            throw new IllegalStateException(
                    "Somente serviços ativos podem ser contratados"
            );
        }

        Contratacao contratacao = new Contratacao();

        contratacao.setServico(servico);
        contratacao.setContratante(contratante);
        contratacao.setSituacao(
                SituacaoContratacao.SOLICITADA
        );

        return new ContratacaoResponse(
                contratacaoRepository.save(contratacao)
        );
    }

    public List<ContratacaoResponse> listar(
            Usuario usuario
    ) {
        return contratacaoRepository
                .findAll()
                .stream()
                .filter(contratacao ->
                        podeVisualizar(contratacao, usuario)
                )
                .map(ContratacaoResponse::new)
                .toList();
    }

    public ContratacaoResponse buscarPorId(
            Long id,
            Usuario usuario
    ) {
        Contratacao contratacao =
                buscarContratacao(id);

        if (!podeVisualizar(contratacao, usuario)) {
            throw new AccessDeniedException(
                    "Você não possui permissão para visualizar esta contratação"
            );
        }

        return new ContratacaoResponse(contratacao);
    }

    public ContratacaoResponse alterarSituacao(
            Long id,
            SituacaoContratacao novaSituacao,
            Usuario usuario
    ) {
        Contratacao contratacao =
                buscarContratacao(id);

        if (usuario.getRole() == Role.ADMIN) {
            validarTransicao(
                    contratacao.getSituacao(),
                    novaSituacao
            );

            contratacao.setSituacao(novaSituacao);

            return new ContratacaoResponse(
                    contratacaoRepository.save(contratacao)
            );
        }

        boolean ehContratante =
                contratacao.getContratante()
                        .getId()
                        .equals(usuario.getId());

        boolean ehPrestador =
                contratacao.getServico()
                        .getPrestador()
                        .getId()
                        .equals(usuario.getId());

        SituacaoContratacao atual =
                contratacao.getSituacao();

        if (atual == SituacaoContratacao.SOLICITADA) {

            if (novaSituacao == SituacaoContratacao.ACEITA
                    || novaSituacao == SituacaoContratacao.RECUSADA) {

                if (!ehPrestador) {
                    throw new AccessDeniedException(
                            "Somente o prestador pode aceitar ou recusar a contratação"
                    );
                }

            } else if (novaSituacao == SituacaoContratacao.CANCELADA) {

                if (!ehContratante) {
                    throw new AccessDeniedException(
                            "Somente o contratante pode cancelar a solicitação"
                    );
                }

            } else {
                throw new IllegalStateException(
                        "Transição de situação inválida"
                );
            }

        } else if (atual == SituacaoContratacao.ACEITA) {

            if (novaSituacao == SituacaoContratacao.CONCLUIDA) {

                if (!ehPrestador && !ehContratante) {
                    throw new AccessDeniedException(
                            "Você não possui permissão para concluir esta contratação"
                    );
                }

            } else if (novaSituacao == SituacaoContratacao.CANCELADA) {

                if (!ehContratante) {
                    throw new AccessDeniedException(
                            "Somente o contratante pode cancelar a contratação"
                    );
                }

            } else {
                throw new IllegalStateException(
                        "Transição de situação inválida"
                );
            }

        } else {
            throw new IllegalStateException(
                    "Esta contratação já está em uma situação final"
            );
        }

        contratacao.setSituacao(novaSituacao);

        return new ContratacaoResponse(
                contratacaoRepository.save(contratacao)
        );
    }

    private void validarTransicao(
            SituacaoContratacao atual,
            SituacaoContratacao nova
    ) {
        boolean valida = false;

        if (atual == SituacaoContratacao.SOLICITADA) {
            valida =
                    nova == SituacaoContratacao.ACEITA
                            || nova == SituacaoContratacao.RECUSADA
                            || nova == SituacaoContratacao.CANCELADA;
        }

        if (atual == SituacaoContratacao.ACEITA) {
            valida =
                    nova == SituacaoContratacao.CONCLUIDA
                            || nova == SituacaoContratacao.CANCELADA;
        }

        if (!valida) {
            throw new IllegalStateException(
                    "Transição de situação inválida"
            );
        }
    }

    private Contratacao buscarContratacao(Long id) {
        return contratacaoRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Contratação não encontrada"
                        )
                );
    }

    private boolean podeVisualizar(
            Contratacao contratacao,
            Usuario usuario
    ) {
        boolean admin =
                usuario.getRole() == Role.ADMIN;

        boolean contratante =
                contratacao.getContratante()
                        .getId()
                        .equals(usuario.getId());

        boolean prestador =
                contratacao.getServico()
                        .getPrestador()
                        .getId()
                        .equals(usuario.getId());

        return admin || contratante || prestador;
    }
}