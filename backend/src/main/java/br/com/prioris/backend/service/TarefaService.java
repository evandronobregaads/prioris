package br.com.prioris.backend.service;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.entity.Meta;
import br.com.prioris.backend.entity.Objetivo;
import br.com.prioris.backend.entity.Tarefa;
import br.com.prioris.backend.entity.Usuario;
import br.com.prioris.backend.exception.RecursoNaoEncontradoException;
import br.com.prioris.backend.repository.MetaRepository;
import br.com.prioris.backend.repository.ObjetivoRepository;
import br.com.prioris.backend.repository.TarefaRepository;
import br.com.prioris.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MetaRepository metaRepository;
    private final ObjetivoRepository objetivoRepository;

    public TarefaService(
            TarefaRepository tarefaRepository,
            UsuarioRepository usuarioRepository,
            MetaRepository metaRepository,
            ObjetivoRepository objetivoRepository
    ) {
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
        this.metaRepository = metaRepository;
        this.objetivoRepository = objetivoRepository;
    }

    public List<TarefaResponseDTO> listarTodos(Long idUsuario) {

        buscarUsuarioAtivo(idUsuario);

        return tarefaRepository
                .findAllByUsuario_IdUsuarioAndStatusNotOrderByIdTarefaAsc(
                        idUsuario,
                        "ELIMINADA"
                )
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public TarefaResponseDTO buscarPorId(
            Long idUsuario,
            Long idTarefa
    ) {

        buscarUsuarioAtivo(idUsuario);

        Tarefa tarefa =
                buscarTarefaNaoEliminada(idUsuario, idTarefa);

        return converterParaResponse(tarefa);
    }

    public TarefaResponseDTO cadastrar(
            Long idUsuario,
            TarefaRequestDTO dto
    ) {

        Usuario usuario = buscarUsuarioAtivo(idUsuario);

        validarVinculos(
                dto.getIdMeta(),
                dto.getIdObjetivo()
        );

        Meta meta = null;
        Objetivo objetivo = null;

        if (dto.getIdMeta() != null) {
            meta = buscarMetaValida(
                    idUsuario,
                    dto.getIdMeta()
            );
        }

        if (dto.getIdObjetivo() != null) {
            objetivo = buscarObjetivoValido(
                    idUsuario,
                    dto.getIdObjetivo()
            );
        }

        Tarefa tarefa = new Tarefa();

        tarefa.setUsuario(usuario);
        tarefa.setMeta(meta);
        tarefa.setObjetivo(objetivo);
        tarefa.setTitulo(dto.getTitulo().trim());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setClassificacaoAbcde(dto.getClassificacaoAbcde());
        tarefa.setDataPlanejada(dto.getDataPlanejada());
        tarefa.setPrazo(dto.getPrazo());
        tarefa.setTempoEstimado(dto.getTempoEstimado());
        tarefa.setStatus("PENDENTE");

        Tarefa tarefaSalva =
                tarefaRepository.save(tarefa);

        return converterParaResponse(tarefaSalva);
    }

    public TarefaResponseDTO atualizar(
            Long idUsuario,
            Long idTarefa,
            TarefaAtualizacaoDTO dto
    ) {

        buscarUsuarioAtivo(idUsuario);

        Tarefa tarefa =
                buscarTarefaNaoEliminada(idUsuario, idTarefa);

        validarVinculos(
                dto.getIdMeta(),
                dto.getIdObjetivo()
        );

        Meta meta = null;
        Objetivo objetivo = null;

        if (dto.getIdMeta() != null) {
            meta = buscarMetaValida(
                    idUsuario,
                    dto.getIdMeta()
            );
        }

        if (dto.getIdObjetivo() != null) {
            objetivo = buscarObjetivoValido(
                    idUsuario,
                    dto.getIdObjetivo()
            );
        }

        tarefa.setMeta(meta);
        tarefa.setObjetivo(objetivo);
        tarefa.setTitulo(dto.getTitulo().trim());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setClassificacaoAbcde(dto.getClassificacaoAbcde());
        tarefa.setDataPlanejada(dto.getDataPlanejada());
        tarefa.setPrazo(dto.getPrazo());
        tarefa.setTempoEstimado(dto.getTempoEstimado());

        if (dto.getStatus() != null) {
            tarefa.setStatus(dto.getStatus().toUpperCase());
        }

        Tarefa tarefaAtualizada =
                tarefaRepository.save(tarefa);

        return converterParaResponse(tarefaAtualizada);
    }

    public TarefaResponseDTO atualizarParcialmente(
            Long idUsuario,
            Long idTarefa,
            TarefaPatchDTO dto
    ) {

        buscarUsuarioAtivo(idUsuario);

        Tarefa tarefa =
                buscarTarefaNaoEliminada(idUsuario, idTarefa);

        validarVinculos(
                dto.getIdMeta(),
                dto.getIdObjetivo()
        );

        if (dto.getTitulo() != null) {

            String titulo = dto.getTitulo().trim();

            if (titulo.isBlank()) {
                throw new IllegalArgumentException(
                        "O título não pode ficar vazio"
                );
            }

            tarefa.setTitulo(titulo);
        }

        if (dto.getDescricao() != null) {
            tarefa.setDescricao(dto.getDescricao());
        }

        if (dto.getClassificacaoAbcde() != null) {
            tarefa.setClassificacaoAbcde(
                    dto.getClassificacaoAbcde().toUpperCase()
            );
        }

        if (dto.getDataPlanejada() != null) {
            tarefa.setDataPlanejada(dto.getDataPlanejada());
        }

        if (dto.getPrazo() != null) {
            tarefa.setPrazo(dto.getPrazo());
        }

        if (dto.getTempoEstimado() != null) {
            tarefa.setTempoEstimado(dto.getTempoEstimado());
        }

        if (dto.getStatus() != null) {
            tarefa.setStatus(
                    dto.getStatus().toUpperCase()
            );
        }

        if (dto.getIdMeta() != null) {

            Meta meta = buscarMetaValida(
                    idUsuario,
                    dto.getIdMeta()
            );

            tarefa.setMeta(meta);
            tarefa.setObjetivo(null);
        }

        if (dto.getIdObjetivo() != null) {

            Objetivo objetivo = buscarObjetivoValido(
                    idUsuario,
                    dto.getIdObjetivo()
            );

            tarefa.setObjetivo(objetivo);
            tarefa.setMeta(null);
        }

        Tarefa tarefaAtualizada =
                tarefaRepository.save(tarefa);

        return converterParaResponse(tarefaAtualizada);
    }

    public void excluir(
            Long idUsuario,
            Long idTarefa
    ) {

        buscarUsuarioAtivo(idUsuario);

        Tarefa tarefa =
                buscarTarefaNaoEliminada(idUsuario, idTarefa);

        tarefa.setStatus("ELIMINADA");

        tarefaRepository.save(tarefa);
    }

    private void validarVinculos(
            Long idMeta,
            Long idObjetivo
    ) {

        if (idMeta != null && idObjetivo != null) {
            throw new IllegalArgumentException(
                    "A tarefa não pode estar vinculada a uma meta "
                            + "e a um objetivo ao mesmo tempo"
            );
        }
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

    private Objetivo buscarObjetivoValido(
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

    private Meta buscarMetaValida(
            Long idUsuario,
            Long idMeta
    ) {

        return metaRepository
                .findByIdMetaAndObjetivo_Usuario_IdUsuarioAndStatusNotAndObjetivo_StatusNot(
                        idMeta,
                        idUsuario,
                        "CANCELADA",
                        "CANCELADO"
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Meta não encontrada com o id: "
                                        + idMeta
                        )
                );
    }

    private Tarefa buscarTarefaNaoEliminada(
            Long idUsuario,
            Long idTarefa
    ) {

        return tarefaRepository
                .findByIdTarefaAndUsuario_IdUsuarioAndStatusNot(
                        idTarefa,
                        idUsuario,
                        "ELIMINADA"
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Tarefa não encontrada com o id: "
                                        + idTarefa
                        )
                );
    }

    private TarefaResponseDTO converterParaResponse(
            Tarefa tarefa
    ) {

        Long idMeta = tarefa.getMeta() != null
                ? tarefa.getMeta().getIdMeta()
                : null;

        Long idObjetivo = tarefa.getObjetivo() != null
                ? tarefa.getObjetivo().getIdObjetivo()
                : null;

        return new TarefaResponseDTO(
                tarefa.getIdTarefa(),
                tarefa.getUsuario().getIdUsuario(),
                idMeta,
                idObjetivo,
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getClassificacaoAbcde(),
                tarefa.getDataPlanejada(),
                tarefa.getPrazo(),
                tarefa.getTempoEstimado(),
                tarefa.getStatus(),
                tarefa.getDataCriacao(),
                tarefa.getDataConclusao()
        );
    }
}