package br.com.prioris.backend.service;
import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.entity.Objetivo;
import br.com.prioris.backend.exception.RecursoNaoEncontradoException;

import br.com.prioris.backend.entity.Usuario;
import br.com.prioris.backend.repository.ObjetivoRepository;
import br.com.prioris.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import br.com.prioris.backend.exception.EmailJaCadastradoException;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Service
    public static class ObjetivoService {

        private final ObjetivoRepository objetivoRepository;
        private final UsuarioRepository usuarioRepository;

        public ObjetivoService(
                ObjetivoRepository objetivoRepository,
                UsuarioRepository usuarioRepository
        ) {
            this.objetivoRepository = objetivoRepository;
            this.usuarioRepository = usuarioRepository;
        }

        public List<ObjetivoResponseDTO> listarTodos(Long idUsuario) {

            buscarUsuarioAtivo(idUsuario);

            return objetivoRepository
                    .findAllByUsuario_IdUsuarioOrderByIdObjetivoAsc(idUsuario)
                    .stream()
                    .map(this::converterParaResponse)
                    .toList();
        }

        public ObjetivoResponseDTO buscarPorId(
                Long idUsuario,
                Long idObjetivo
        ) {

            buscarUsuarioAtivo(idUsuario);

            Objetivo objetivo = objetivoRepository
                    .findByIdObjetivoAndUsuario_IdUsuario(
                            idObjetivo,
                            idUsuario
                    )
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Objetivo não encontrado com o id: "
                                            + idObjetivo
                            )
                    );

            return converterParaResponse(objetivo);
        }

        public ObjetivoResponseDTO cadastrar(
                Long idUsuario,
                ObjetivoRequestDTO dto
        ) {

            Usuario usuario = buscarUsuarioAtivo(idUsuario);

            Objetivo objetivo = new Objetivo();

            objetivo.setUsuario(usuario);
            objetivo.setTitulo(dto.getTitulo().trim());
            objetivo.setDescricao(dto.getDescricao());
            objetivo.setArea(dto.getArea().trim().toUpperCase());
            objetivo.setMotivo(dto.getMotivo());
            objetivo.setPrazo(dto.getPrazo());
            objetivo.setStatus("ATIVO");

            Objetivo objetivoSalvo =
                    objetivoRepository.save(objetivo);

            return converterParaResponse(objetivoSalvo);
        }

        private Usuario buscarUsuarioAtivo(Long idUsuario) {

            return usuarioRepository
                    .findByIdUsuarioAndAtivoTrue(idUsuario)
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Usuário não encontrado com o id: "
                                            + idUsuario
                            )
                    );
        }

        private ObjetivoResponseDTO converterParaResponse(
                Objetivo objetivo
        ) {

            return new ObjetivoResponseDTO(
                    objetivo.getIdObjetivo(),
                    objetivo.getUsuario().getIdUsuario(),
                    objetivo.getTitulo(),
                    objetivo.getDescricao(),
                    objetivo.getArea(),
                    objetivo.getMotivo(),
                    objetivo.getPrazo(),
                    objetivo.getStatus(),
                    objetivo.getDataCriacao()
            );
        }
    }
}

