package br.com.fiap.campusgigs.service;

import br.com.fiap.campusgigs.client.ViaCepClient;
import br.com.fiap.campusgigs.dto.CadastroRequest;
import br.com.fiap.campusgigs.dto.LoginRequest;
import br.com.fiap.campusgigs.dto.LoginResponse;
import br.com.fiap.campusgigs.dto.ViaCepResponse;
import br.com.fiap.campusgigs.model.Role;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final ViaCepClient viaCepClient;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService,
            ViaCepClient viaCepClient
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.viaCepClient = viaCepClient;
    }

    public Usuario cadastrar(CadastroRequest request) {

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "E-mail já cadastrado"
            );
        }

        String cep = limparCep(request.cep());

        if (cep.length() != 8) {
            throw new IllegalArgumentException(
                    "CEP deve possuir 8 dígitos"
            );
        }

        ViaCepResponse endereco;

        try {
            endereco = viaCepClient.buscarCep(cep);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Não foi possível consultar o serviço de CEP"
            );
        }

        if (endereco == null
                || Boolean.TRUE.equals(endereco.erro())
                || endereco.localidade() == null
                || endereco.uf() == null) {

            throw new IllegalArgumentException(
                    "CEP não encontrado"
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(
                passwordEncoder.encode(request.senha())
        );

        usuario.setCep(cep);
        usuario.setCidade(endereco.localidade());
        usuario.setUf(endereco.uf());

        usuario.setRole(Role.USER);

        return usuarioRepository.save(usuario);
    }

    public LoginResponse autenticar(LoginRequest request) {

        Usuario usuario = usuarioRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "E-mail ou senha inválidos"
                        )
                );

        if (!passwordEncoder.matches(
                request.senha(),
                usuario.getSenha()
        )) {
            throw new IllegalArgumentException(
                    "E-mail ou senha inválidos"
            );
        }

        String token =
                tokenService.gerarToken(usuario);

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name(),
                token,
                "Login realizado com sucesso"
        );
    }

    private String limparCep(String cep) {

        if (cep == null) {
            throw new IllegalArgumentException(
                    "CEP é obrigatório"
            );
        }

        return cep.replaceAll("\\D", "");
    }
}