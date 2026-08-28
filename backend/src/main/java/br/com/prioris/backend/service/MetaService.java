package br.com.prioris.backend.service;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.entity.Meta;
import br.com.prioris.backend.entity.Objetivo;
import br.com.prioris.backend.exception.RecursoNaoEncontradoException;
import br.com.prioris.backend.repository.MetaRepository;
import br.com.prioris.backend.repository.ObjetivoRepository;
import br.com.prioris.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaService {

    private final MetaRepository metaRepository;
    private final ObjetivoRepository objetivoRepository;
    private final UsuarioRepository usuarioRepository;

    public MetaService(
            MetaRepository metaRepository,
            ObjetivoRepository objetivoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.metaRepository = metaRepository;
        this.objetivoRepository = objetivoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<MetaResponseDTO> listarTodos(
            Long idUsuario,
            Long idObjetivo
    ) {

        buscarObjetivoValido(idUsuario, idObjetivo);

        return metaRepository
                .findAllByObjetivo_IdObjetivoAndObjetivo_Usuario_IdUsuarioAndStatusNotOrderByIdMetaAsc(
                        idObjetivo,
                        idUsuario,
                        "CANCELADA"
                )
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public MetaResponseDTO buscarPorId(
            Long idUsuario,
            Long idObjetivo,
            Long idMeta
    ) {

        buscarObjetivoValido(idUsuario, idObjetivo);

        Meta meta = buscarMetaNaoCancelada(
                idUsuario,
                idObjetivo,
                idMeta
        );

        return converterParaResponse(meta);
    }

    public MetaResponseDTO cadastrar(
            Long idUsuario,
            Long idObjetivo,
            MetaRequestDTO dto
    ) {

        Objetivo objetivo =
                buscarObjetivoValido(idUsuario, idObjetivo);

        Meta meta = new Meta();

        meta.setObjetivo(objetivo);
        meta.setTitulo(dto.getTitulo().trim());
        meta.setDescricao(dto.getDescricao());
        meta.setPrazo(dto.getPrazo());
        meta.setStatus("PENDENTE");

        Meta metaSalva = metaRepository.save(meta);

        return converterParaResponse(metaSalva);
    }

    public MetaResponseDTO atualizar(
            Long idUsuario,
            Long idObjetivo,
            Long idMeta,
            MetaAtualizacaoDTO dto
    ) {

        buscarObjetivoValido(idUsuario, idObjetivo);

        Meta meta = buscarMetaNaoCancelada(
                idUsuario,
                idObjetivo,
                idMeta
        );

        meta.setTitulo(dto.getTitulo().trim());
        meta.setDescricao(dto.getDescricao());
        meta.setPrazo(dto.getPrazo());

        if (dto.getStatus() != null) {
            meta.setStatus(
                    dto.getStatus().trim().toUpperCase()
            );
        }

        Meta metaAtualizada = metaRepository.save(meta);

        return converterParaResponse(metaAtualizada);
    }

    public MetaResponseDTO atualizarParcialmente(
            Long idUsuario,
            Long idObjetivo,
            Long idMeta,
            MetaPatchDTO dto
    ) {

        buscarObjetivoValido(idUsuario, idObjetivo);

        Meta meta = buscarMetaNaoCancelada(
                idUsuario,
                idObjetivo,
                idMeta
        );

        if (dto.getTitulo() != null) {

            String titulo = dto.getTitulo().trim();

            if (titulo.isBlank()) {
                throw new IllegalArgumentException(
                        "O título não pode ficar vazio"
                );
            }

            meta.setTitulo(titulo);
        }

        if (dto.getDescricao() != null) {
            meta.setDescricao(dto.getDescricao());
        }

        if (dto.getPrazo() != null) {
            meta.setPrazo(dto.getPrazo());
        }

        if (dto.getStatus() != null) {
            meta.setStatus(
                    dto.getStatus().trim().toUpperCase()
            );
        }

        Meta metaAtualizada = metaRepository.save(meta);

        return converterParaResponse(metaAtualizada);
    }

    public void excluir(
            Long idUsuario,
            Long idObjetivo,
            Long idMeta
    ) {

        buscarObjetivoValido(idUsuario, idObjetivo);

        Meta meta = buscarMetaNaoCancelada(
                idUsuario,
                idObjetivo,
                idMeta
        );

        meta.setStatus("CANCELADA");

        metaRepository.save(meta);
    }

    private Objetivo buscarObjetivoValido(
            Long idUsuario,
            Long idObjetivo
    ) {

        usuarioRepository
                .findByIdUsuarioAndAtivoTrue(idUsuario)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado com o id: "
                                        + idUsuario
                        )
                );

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

    private Meta buscarMetaNaoCancelada(
            Long idUsuario,
            Long idObjetivo,
            Long idMeta
    ) {

        return metaRepository
                .findByIdMetaAndObjetivo_IdObjetivoAndObjetivo_Usuario_IdUsuarioAndStatusNot(
                        idMeta,
                        idObjetivo,
                        idUsuario,
                        "CANCELADA"
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Meta não encontrada com o id: "
                                        + idMeta
                        )
                );
    }

    private MetaResponseDTO converterParaResponse(Meta meta) {

        return new MetaResponseDTO(
                meta.getIdMeta(),
                meta.getObjetivo().getIdObjetivo(),
                meta.getObjetivo()
                        .getUsuario()
                        .getIdUsuario(),
                meta.getTitulo(),
                meta.getDescricao(),
                meta.getPrazo(),
                meta.getStatus(),
                meta.getDataCriacao()
        );
    }
}