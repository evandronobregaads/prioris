package br.com.prioris.backend.service;

import br.com.prioris.backend.dto.PrioridadeDiariaRequestDTO;
import br.com.prioris.backend.dto.PrioridadeDiariaResponseDTO;
import br.com.prioris.backend.entity.PrioridadeDiaria;
import br.com.prioris.backend.entity.Tarefa;
import br.com.prioris.backend.entity.Usuario;
import br.com.prioris.backend.exception.PrioridadeDiariaJaDefinidaException;
import br.com.prioris.backend.exception.RecursoNaoEncontradoException;
import br.com.prioris.backend.repository.PrioridadeDiariaRepository;
import br.com.prioris.backend.repository.TarefaRepository;
import br.com.prioris.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrioridadeDiariaService {

    private final PrioridadeDiariaRepository prioridadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final TarefaRepository tarefaRepository;

    public PrioridadeDiariaService(
            PrioridadeDiariaRepository prioridadeRepository,
            UsuarioRepository usuarioRepository,
            TarefaRepository tarefaRepository
    ) {
        this.prioridadeRepository = prioridadeRepository;
        this.usuarioRepository = usuarioRepository;
        this.tarefaRepository = tarefaRepository;
    }

    public PrioridadeDiariaResponseDTO definir(
            Long idUsuario,
            PrioridadeDiariaRequestDTO dto
    ) {

        Usuario usuario = buscarUsuarioAtivo(idUsuario);

        LocalDate hoje = LocalDate.now();

        if (prioridadeRepository
                .existsByUsuario_IdUsuarioAndDataPrioridade(
                        idUsuario,
                        hoje
                )) {

            throw new PrioridadeDiariaJaDefinidaException(
                    "Já existe uma Prioridade #1 definida para hoje"
            );
        }

        Tarefa tarefa =
                buscarTarefaValida(
                        idUsuario,
                        dto.getIdTarefa()
                );

        validarTarefaParaPrioridade(tarefa);

        PrioridadeDiaria prioridade =
                new PrioridadeDiaria();

        prioridade.setUsuario(usuario);
        prioridade.setTarefa(tarefa);
        prioridade.setDataPrioridade(hoje);

        PrioridadeDiaria prioridadeSalva =
                prioridadeRepository.save(prioridade);

        return converterParaResponse(prioridadeSalva);
    }

    public PrioridadeDiariaResponseDTO buscarHoje(
            Long idUsuario
    ) {

        buscarUsuarioAtivo(idUsuario);

        LocalDate hoje = LocalDate.now();

        PrioridadeDiaria prioridade =
                prioridadeRepository
                        .findByUsuario_IdUsuarioAndDataPrioridade(
                                idUsuario,
                                hoje
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Nenhuma Prioridade #1 "
                                                + "foi definida para hoje"
                                )
                        );

        return converterParaResponse(prioridade);
    }

    public PrioridadeDiariaResponseDTO alterarHoje(
            Long idUsuario,
            PrioridadeDiariaRequestDTO dto
    ) {

        buscarUsuarioAtivo(idUsuario);

        LocalDate hoje = LocalDate.now();

        PrioridadeDiaria prioridade =
                prioridadeRepository
                        .findByUsuario_IdUsuarioAndDataPrioridade(
                                idUsuario,
                                hoje
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Nenhuma Prioridade #1 "
                                                + "foi definida para hoje"
                                )
                        );

        Tarefa tarefa =
                buscarTarefaValida(
                        idUsuario,
                        dto.getIdTarefa()
                );

        validarTarefaParaPrioridade(tarefa);

        prioridade.setTarefa(tarefa);

        PrioridadeDiaria prioridadeAtualizada =
                prioridadeRepository.save(prioridade);

        return converterParaResponse(prioridadeAtualizada);
    }

    public void excluirHoje(Long idUsuario) {

        buscarUsuarioAtivo(idUsuario);

        LocalDate hoje = LocalDate.now();

        PrioridadeDiaria prioridade =
                prioridadeRepository
                        .findByUsuario_IdUsuarioAndDataPrioridade(
                                idUsuario,
                                hoje
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Nenhuma Prioridade #1 "
                                                + "foi definida para hoje"
                                )
                        );

        prioridadeRepository.delete(prioridade);
    }

    public List<PrioridadeDiariaResponseDTO> listarHistorico(
            Long idUsuario
    ) {

        buscarUsuarioAtivo(idUsuario);

        return prioridadeRepository
                .findAllByUsuario_IdUsuarioOrderByDataPrioridadeDesc(
                        idUsuario
                )
                .stream()
                .map(this::converterParaResponse)
                .toList();
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

    private Tarefa buscarTarefaValida(
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

    private void validarTarefaParaPrioridade(Tarefa tarefa) {

        if (!tarefa.getStatus().equals("PENDENTE")
                && !tarefa.getStatus().equals("EM_ANDAMENTO")) {

            throw new IllegalArgumentException(
                    "Somente tarefas PENDENTES ou EM_ANDAMENTO "
                            + "podem ser definidas como Prioridade #1"
            );
        }
    }

    private PrioridadeDiariaResponseDTO converterParaResponse(
            PrioridadeDiaria prioridade
    ) {

        return new PrioridadeDiariaResponseDTO(
                prioridade.getIdPrioridadeDiaria(),
                prioridade.getUsuario().getIdUsuario(),
                prioridade.getTarefa().getIdTarefa(),
                prioridade.getTarefa().getTitulo(),
                prioridade.getTarefa().getClassificacaoAbcde(),
                prioridade.getTarefa().getStatus(),
                prioridade.getDataPrioridade()
        );
    }
}