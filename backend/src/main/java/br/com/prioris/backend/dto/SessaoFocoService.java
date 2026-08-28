package br.com.prioris.backend.service;

import br.com.prioris.backend.dto.SessaoFocoFinalizacaoDTO;
import br.com.prioris.backend.dto.SessaoFocoRequestDTO;
import br.com.prioris.backend.dto.SessaoFocoResponseDTO;
import br.com.prioris.backend.entity.SessaoFoco;
import br.com.prioris.backend.entity.Tarefa;
import br.com.prioris.backend.entity.Usuario;
import br.com.prioris.backend.exception.RecursoNaoEncontradoException;
import br.com.prioris.backend.repository.SessaoFocoRepository;
import br.com.prioris.backend.repository.TarefaRepository;
import br.com.prioris.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SessaoFocoService {

    private final SessaoFocoRepository sessaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TarefaRepository tarefaRepository;

    public SessaoFocoService(
            SessaoFocoRepository sessaoRepository,
            UsuarioRepository usuarioRepository,
            TarefaRepository tarefaRepository
    ) {
        this.sessaoRepository = sessaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.tarefaRepository = tarefaRepository;
    }

    public SessaoFocoResponseDTO iniciar(
            Long idUsuario,
            SessaoFocoRequestDTO dto
    ) {

        Usuario usuario = buscarUsuarioAtivo(idUsuario);

        Tarefa tarefa = null;

        if (dto.getIdTarefa() != null) {

            tarefa = tarefaRepository
                    .findByIdTarefaAndUsuario_IdUsuarioAndStatusNot(
                            dto.getIdTarefa(),
                            idUsuario,
                            "ELIMINADA"
                    )
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Tarefa não encontrada com o id: "
                                            + dto.getIdTarefa()
                            )
                    );

            validarTarefaParaFoco(tarefa);
        }

        SessaoFoco sessao = new SessaoFoco();

        sessao.setUsuario(usuario);
        sessao.setTarefa(tarefa);
        sessao.setDataInicio(LocalDateTime.now());
        sessao.setTempoFocoPlanejado(
                dto.getTempoFocoPlanejado()
        );
        sessao.setTempoDescansoPlanejado(
                dto.getTempoDescansoPlanejado()
        );
        sessao.setTempoFocoRealizado(0);
        sessao.setStatus("EM_ANDAMENTO");

        return converterParaResponse(
                sessaoRepository.save(sessao)
        );
    }

    public List<SessaoFocoResponseDTO> listarHistorico(
            Long idUsuario
    ) {

        buscarUsuarioAtivo(idUsuario);

        return sessaoRepository
                .findAllByUsuario_IdUsuarioOrderByDataInicioDesc(
                        idUsuario
                )
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public SessaoFocoResponseDTO buscarPorId(
            Long idUsuario,
            Long idSessao
    ) {

        buscarUsuarioAtivo(idUsuario);

        return converterParaResponse(
                buscarSessao(idUsuario, idSessao)
        );
    }

    public SessaoFocoResponseDTO pausar(
            Long idUsuario,
            Long idSessao
    ) {

        buscarUsuarioAtivo(idUsuario);

        SessaoFoco sessao =
                buscarSessao(idUsuario, idSessao);

        if (!"EM_ANDAMENTO".equals(sessao.getStatus())) {
            throw new IllegalArgumentException(
                    "Somente uma sessão em andamento pode ser pausada"
            );
        }

        sessao.setStatus("PAUSADA");

        return converterParaResponse(
                sessaoRepository.save(sessao)
        );
    }

    public SessaoFocoResponseDTO retomar(
            Long idUsuario,
            Long idSessao
    ) {

        buscarUsuarioAtivo(idUsuario);

        SessaoFoco sessao =
                buscarSessao(idUsuario, idSessao);

        if (!"PAUSADA".equals(sessao.getStatus())) {
            throw new IllegalArgumentException(
                    "Somente uma sessão pausada pode ser retomada"
            );
        }

        sessao.setStatus("EM_ANDAMENTO");

        return converterParaResponse(
                sessaoRepository.save(sessao)
        );
    }

    public SessaoFocoResponseDTO finalizar(
            Long idUsuario,
            Long idSessao,
            SessaoFocoFinalizacaoDTO dto
    ) {

        buscarUsuarioAtivo(idUsuario);

        SessaoFoco sessao =
                buscarSessao(idUsuario, idSessao);

        validarSessaoAberta(sessao);

        sessao.setTempoFocoRealizado(
                dto.getTempoFocoRealizado()
        );
        sessao.setDataFim(LocalDateTime.now());
        sessao.setStatus("CONCLUIDA");

        return converterParaResponse(
                sessaoRepository.save(sessao)
        );
    }

    public SessaoFocoResponseDTO interromper(
            Long idUsuario,
            Long idSessao,
            SessaoFocoFinalizacaoDTO dto
    ) {

        buscarUsuarioAtivo(idUsuario);

        SessaoFoco sessao =
                buscarSessao(idUsuario, idSessao);

        validarSessaoAberta(sessao);

        sessao.setTempoFocoRealizado(
                dto.getTempoFocoRealizado()
        );
        sessao.setDataFim(LocalDateTime.now());
        sessao.setStatus("INTERROMPIDA");

        return converterParaResponse(
                sessaoRepository.save(sessao)
        );
    }

    private void validarSessaoAberta(SessaoFoco sessao) {

        if ("CONCLUIDA".equals(sessao.getStatus())
                || "INTERROMPIDA".equals(sessao.getStatus())) {

            throw new IllegalArgumentException(
                    "Esta sessão de foco já foi encerrada"
            );
        }
    }

    private void validarTarefaParaFoco(Tarefa tarefa) {

        if (!"PENDENTE".equals(tarefa.getStatus())
                && !"EM_ANDAMENTO".equals(tarefa.getStatus())) {

            throw new IllegalArgumentException(
                    "Somente tarefas PENDENTES ou EM_ANDAMENTO "
                            + "podem receber uma sessão de foco"
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

    private SessaoFoco buscarSessao(
            Long idUsuario,
            Long idSessao
    ) {

        return sessaoRepository
                .findByIdSessaoFocoAndUsuario_IdUsuario(
                        idSessao,
                        idUsuario
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Sessão de foco não encontrada com o id: "
                                        + idSessao
                        )
                );
    }

    private SessaoFocoResponseDTO converterParaResponse(
            SessaoFoco sessao
    ) {

        Long idTarefa = null;
        String tituloTarefa = null;

        if (sessao.getTarefa() != null) {
            idTarefa = sessao.getTarefa().getIdTarefa();
            tituloTarefa = sessao.getTarefa().getTitulo();
        }

        return new SessaoFocoResponseDTO(
                sessao.getIdSessaoFoco(),
                sessao.getUsuario().getIdUsuario(),
                idTarefa,
                tituloTarefa,
                sessao.getDataInicio(),
                sessao.getDataFim(),
                sessao.getTempoFocoPlanejado(),
                sessao.getTempoDescansoPlanejado(),
                sessao.getTempoFocoRealizado(),
                sessao.getStatus()
        );
    }
}