package br.com.prioris.backend.service;
import br.com.prioris.backend.exception.RecursoNaoEncontradoException;

import br.com.prioris.backend.dto.UsuarioResponseDTO;
import br.com.prioris.backend.entity.Usuario;
import br.com.prioris.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import br.com.prioris.backend.dto.UsuarioRequestDTO;
import br.com.prioris.backend.exception.EmailJaCadastradoException;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.prioris.backend.dto.UsuarioAtualizacaoDTO;
import br.com.prioris.backend.dto.UsuarioPatchDTO;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository
                .findAllByAtivoTrueOrderByIdUsuarioAsc()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {

        Usuario usuario = buscarUsuarioAtivo(id);

        return converterParaResponse(usuario);
    }

    private UsuarioResponseDTO converterParaResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataCriacao(),
                usuario.getAtivo()
        );
    }

    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {

        String emailNormalizado = dto.getEmail()
                .trim()
                .toLowerCase();

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new EmailJaCadastradoException(
                    "Já existe um usuário cadastrado com este e-mail"
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(emailNormalizado);
        usuario.setSenhaHash(
                passwordEncoder.encode(dto.getSenha())
        );
        usuario.setAtivo(true);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return converterParaResponse(usuarioSalvo);
    }

    public UsuarioResponseDTO atualizar(
            Long id,
            UsuarioAtualizacaoDTO dto
    ) {

        Usuario usuario = buscarUsuarioAtivo(id);

        String emailNormalizado = dto.getEmail()
                .trim()
                .toLowerCase();

        if (!usuario.getEmail().equals(emailNormalizado)
                && usuarioRepository.existsByEmail(emailNormalizado)) {

            throw new EmailJaCadastradoException(
                    "Já existe um usuário cadastrado com este e-mail"
            );
        }

        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(emailNormalizado);

        if (dto.getSenha() != null
                && !dto.getSenha().isBlank()) {

            usuario.setSenhaHash(
                    passwordEncoder.encode(dto.getSenha())
            );
        }

        Usuario usuarioAtualizado =
                usuarioRepository.save(usuario);

        return converterParaResponse(usuarioAtualizado);
    }

    public UsuarioResponseDTO atualizarParcialmente(
            Long id,
            UsuarioPatchDTO dto
    ) {

        Usuario usuario = buscarUsuarioAtivo(id);

        if (dto.getNome() != null) {

            String nome = dto.getNome().trim();

            if (nome.isBlank()) {
                throw new IllegalArgumentException(
                        "O nome não pode ficar vazio"
                );
            }

            usuario.setNome(nome);
        }

        if (dto.getEmail() != null) {

            String emailNormalizado = dto.getEmail()
                    .trim()
                    .toLowerCase();

            if (!usuario.getEmail().equals(emailNormalizado)
                    && usuarioRepository.existsByEmail(emailNormalizado)) {

                throw new EmailJaCadastradoException(
                        "Já existe um usuário cadastrado com este e-mail"
                );
            }

            usuario.setEmail(emailNormalizado);
        }

        if (dto.getSenha() != null
                && !dto.getSenha().isBlank()) {

            usuario.setSenhaHash(
                    passwordEncoder.encode(dto.getSenha())
            );
        }

        Usuario usuarioAtualizado =
                usuarioRepository.save(usuario);

        return converterParaResponse(usuarioAtualizado);
    }

    public void excluir(Long id) {

        Usuario usuario = buscarUsuarioAtivo(id);

        usuario.setAtivo(false);

        usuarioRepository.save(usuario);
    }

    private Usuario buscarUsuarioAtivo(Long id) {

        return usuarioRepository
                .findByIdUsuarioAndAtivoTrue(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado com o id: " + id
                        )
                );
    }
}

