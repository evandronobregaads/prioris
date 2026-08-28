package br.com.prioris.backend.service;

import br.com.prioris.backend.dto.ObjetivoRequestDTO;
import br.com.prioris.backend.dto.ObjetivoResponseDTO;
import br.com.prioris.backend.dto.ObjetivoAtualizacaoDTO;
import br.com.prioris.backend.dto.ObjetivoPatchDTO;
import br.com.prioris.backend.entity.Objetivo;
import br.com.prioris.backend.entity.Usuario;
import br.com.prioris.backend.exception.RecursoNaoEncontradoException;
import br.com.prioris.backend.repository.ObjetivoRepository;
import br.com.prioris.backend.repository.UsuarioRepository;
import br.com.prioris.backend.service.ObjetivoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObjetivoService {

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
                .findAllByUsuario_IdUsuarioAndStatusNotOrderByIdObjetivoAsc(
                        idUsuario,
                        "CANCELADO"
                )
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public ObjetivoResponseDTO buscarPorId(
            Long idUsuario,
            Long idObjetivo
    ) {

        buscarUsuarioAtivo(idUsuario);

        Objetivo objetivo =
                buscarObjetivoNaoCancelado(idUsuario, idObjetivo);

        return converterParaResponse(objetivo);
    }

    public ObjetivoResponseDTO cadastrar(
            Long idUsuario,
            ObjetivoRequestDTO dto
    ) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );

        Objetivo objetivo = new Objetivo();

        objetivo.setUsuario(usuario);
        objetivo.setTitulo(dto.getTitulo().trim());
        objetivo.setDescricao(dto.getDescricao());
        objetivo.setArea(dto.getArea());
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

    public List<ObjetivoResponseDTO> listarPorUsuario(Long idUsuario) {

        usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );

        return objetivoRepository
                .findAllByUsuario_IdUsuarioOrderByIdObjetivoAsc(idUsuario)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }
    public ObjetivoResponseDTO atualizar(
            Long idUsuario,
            Long idObjetivo,
            ObjetivoAtualizacaoDTO dto
    ) {

        buscarUsuarioAtivo(idUsuario);

        Objetivo objetivo =
                buscarObjetivoNaoCancelado(idUsuario, idObjetivo);

        objetivo.setTitulo(dto.getTitulo().trim());
        objetivo.setDescricao(dto.getDescricao());
        objetivo.setArea(dto.getArea().trim().toUpperCase());
        objetivo.setMotivo(dto.getMotivo());
        objetivo.setPrazo(dto.getPrazo());

        if (dto.getStatus() != null) {
            objetivo.setStatus(dto.getStatus().toUpperCase());
        }

        Objetivo objetivoAtualizado =
                objetivoRepository.save(objetivo);

        return converterParaResponse(objetivoAtualizado);
    }
    public ObjetivoResponseDTO atualizarParcialmente(
            Long idUsuario,
            Long idObjetivo,
            ObjetivoPatchDTO dto
    ) {

        buscarUsuarioAtivo(idUsuario);

        Objetivo objetivo =
                buscarObjetivoNaoCancelado(idUsuario, idObjetivo);

        if (dto.getTitulo() != null) {

            String titulo = dto.getTitulo().trim();

            if (titulo.isBlank()) {
                throw new IllegalArgumentException(
                        "O título não pode ficar vazio"
                );
            }

            objetivo.setTitulo(titulo);
        }

        if (dto.getDescricao() != null) {
            objetivo.setDescricao(dto.getDescricao());
        }

        if (dto.getArea() != null) {

            String area = dto.getArea().trim();

            if (area.isBlank()) {
                throw new IllegalArgumentException(
                        "A área não pode ficar vazia"
                );
            }

            objetivo.setArea(area.toUpperCase());
        }

        if (dto.getMotivo() != null) {
            objetivo.setMotivo(dto.getMotivo());
        }

        if (dto.getPrazo() != null) {
            objetivo.setPrazo(dto.getPrazo());
        }

        if (dto.getStatus() != null) {
            objetivo.setStatus(
                    dto.getStatus().trim().toUpperCase()
            );
        }

        Objetivo objetivoAtualizado =
                objetivoRepository.save(objetivo);

        return converterParaResponse(objetivoAtualizado);
    }
    public void excluir(
            Long idUsuario,
            Long idObjetivo
    ) {

        buscarUsuarioAtivo(idUsuario);

        Objetivo objetivo =
                buscarObjetivoNaoCancelado(idUsuario, idObjetivo);

        objetivo.setStatus("CANCELADO");

        objetivoRepository.save(objetivo);
    }
    private Objetivo buscarObjetivoNaoCancelado(
            Long idUsuario,
            Long idObjetivo
    ) {

        return objetivoRepository
                .findByIdObjetivoAndUsuario_IdUsuarioAndStatusNot(
                        idObjetivo,
                        idUsuario,
                        "CANCELADO"
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Objetivo não encontrado com o id: "
                                        + idObjetivo
                        )
                );
    }
}