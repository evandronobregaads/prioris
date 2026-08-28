package br.com.prioris.backend.service;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.entity.*;
import br.com.prioris.backend.exception.RecursoNaoEncontradoException;
import br.com.prioris.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class PlanejamentoSemanalService {

    private final PlanejamentoSemanalRepository planejamentoRepository;
    private final PlanejamentoTarefaRepository planejamentoTarefaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CicloRepository cicloRepository;
    private final TarefaRepository tarefaRepository;

    public PlanejamentoSemanalService(
            PlanejamentoSemanalRepository planejamentoRepository,
            PlanejamentoTarefaRepository planejamentoTarefaRepository,
            UsuarioRepository usuarioRepository,
            CicloRepository cicloRepository,
            TarefaRepository tarefaRepository
    ) {
        this.planejamentoRepository = planejamentoRepository;
        this.planejamentoTarefaRepository = planejamentoTarefaRepository;
        this.usuarioRepository = usuarioRepository;
        this.cicloRepository = cicloRepository;
        this.tarefaRepository = tarefaRepository;
    }

    public List<PlanejamentoSemanalResponseDTO> listarTodos(
            Long idUsuario
    ) {

        buscarUsuarioAtivo(idUsuario);

        return planejamentoRepository
                .findAllByUsuario_IdUsuarioOrderByDataInicioSemanaDesc(
                        idUsuario
                )
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public PlanejamentoSemanalResponseDTO buscarPorId(
            Long idUsuario,
            Long idPlanejamento
    ) {

        buscarUsuarioAtivo(idUsuario);

        return converterParaResponse(
                buscarPlanejamentoValido(
                        idUsuario,
                        idPlanejamento
                )
        );
    }

    public PlanejamentoSemanalResponseDTO cadastrar(
            Long idUsuario,
            PlanejamentoSemanalRequestDTO dto
    ) {

        Usuario usuario =
                buscarUsuarioAtivo(idUsuario);

        validarCicloESemana(
                dto.getIdCiclo(),
                dto.getSemanaCiclo()
        );

        Ciclo ciclo = null;

        if (dto.getIdCiclo() != null) {

            ciclo = cicloRepository
                    .findByIdCicloAndUsuario_IdUsuarioAndStatusNot(
                            dto.getIdCiclo(),
                            idUsuario,
                            "CANCELADO"
                    )
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Ciclo não encontrado com o id: "
                                            + dto.getIdCiclo()
                            )
                    );
        }

        PlanejamentoSemanal planejamento =
                new PlanejamentoSemanal();

        planejamento.setUsuario(usuario);
        planejamento.setCiclo(ciclo);
        planejamento.setSemanaCiclo(
                dto.getSemanaCiclo()
        );

        planejamento.setDataInicioSemana(
                dto.getDataInicioSemana()
        );

        planejamento.setDataFimSemana(
                dto.getDataInicioSemana().plusDays(6)
        );

        PlanejamentoSemanal planejamentoSalvo =
                planejamentoRepository.save(planejamento);

        return converterParaResponse(
                planejamentoSalvo
        );
    }

    public PlanejamentoTarefaResponseDTO adicionarTarefa(
            Long idUsuario,
            Long idPlanejamento,
            Long idTarefa
    ) {

        buscarUsuarioAtivo(idUsuario);

        PlanejamentoSemanal planejamento =
                buscarPlanejamentoValido(
                        idUsuario,
                        idPlanejamento
                );

        Tarefa tarefa = tarefaRepository
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

        boolean jaExiste =
                planejamentoTarefaRepository
                        .existsByPlanejamentoSemanal_IdPlanejamentoSemanalAndTarefa_IdTarefa(
                                idPlanejamento,
                                idTarefa
                        );

        if (jaExiste) {
            throw new IllegalArgumentException(
                    "A tarefa já está incluída neste planejamento semanal"
            );
        }

        PlanejamentoTarefa associacao =
                new PlanejamentoTarefa();

        associacao.setPlanejamentoSemanal(
                planejamento
        );

        associacao.setTarefa(tarefa);

        PlanejamentoTarefa associacaoSalva =
                planejamentoTarefaRepository.save(
                        associacao
                );

        return converterTarefaParaResponse(
                associacaoSalva
        );
    }

    public List<PlanejamentoTarefaResponseDTO> listarTarefas(
            Long idUsuario,
            Long idPlanejamento
    ) {

        buscarUsuarioAtivo(idUsuario);

        buscarPlanejamentoValido(
                idUsuario,
                idPlanejamento
        );

        return planejamentoTarefaRepository
                .findAllByPlanejamentoSemanal_IdPlanejamentoSemanalOrderByIdPlanejamentoTarefaAsc(
                        idPlanejamento
                )
                .stream()
                .map(this::converterTarefaParaResponse)
                .toList();
    }

    public void removerTarefa(
            Long idUsuario,
            Long idPlanejamento,
            Long idTarefa
    ) {

        buscarUsuarioAtivo(idUsuario);

        buscarPlanejamentoValido(
                idUsuario,
                idPlanejamento
        );

        PlanejamentoTarefa associacao =
                planejamentoTarefaRepository
                        .findByPlanejamentoSemanal_IdPlanejamentoSemanalAndTarefa_IdTarefa(
                                idPlanejamento,
                                idTarefa
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "A tarefa não está incluída "
                                                + "neste planejamento"
                                )
                        );

        planejamentoTarefaRepository.delete(
                associacao
        );
    }

    private void validarCicloESemana(
            Long idCiclo,
            Byte semanaCiclo
    ) {

        if (idCiclo == null && semanaCiclo != null) {
            throw new IllegalArgumentException(
                    "A semana do ciclo só pode ser informada "
                            + "quando houver um ciclo"
            );
        }

        if (idCiclo != null && semanaCiclo == null) {
            throw new IllegalArgumentException(
                    "Informe a semana do ciclo"
            );
        }
    }

    private Usuario buscarUsuarioAtivo(
            Long idUsuario
    ) {

        return usuarioRepository
                .findByIdUsuarioAndAtivoTrue(idUsuario)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado com o id: "
                                        + idUsuario
                        )
                );
    }

    private PlanejamentoSemanal buscarPlanejamentoValido(
            Long idUsuario,
            Long idPlanejamento
    ) {

        return planejamentoRepository
                .findByIdPlanejamentoSemanalAndUsuario_IdUsuario(
                        idPlanejamento,
                        idUsuario
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Planejamento semanal não encontrado "
                                        + "com o id: "
                                        + idPlanejamento
                        )
                );
    }

    private PlanejamentoSemanalResponseDTO converterParaResponse(
            PlanejamentoSemanal planejamento
    ) {

        List<PlanejamentoTarefa> tarefas =
                planejamentoTarefaRepository
                        .findAllByPlanejamentoSemanal_IdPlanejamentoSemanalOrderByIdPlanejamentoTarefaAsc(
                                planejamento.getIdPlanejamentoSemanal()
                        );

        int totalPlanejadas =
                tarefas.size();

        int totalConcluidas =
                (int) tarefas
                        .stream()
                        .filter(associacao ->
                                "CONCLUIDA".equals(
                                        associacao
                                                .getTarefa()
                                                .getStatus()
                                )
                        )
                        .count();

        BigDecimal score =
                calcularScore(
                        totalPlanejadas,
                        totalConcluidas
                );

        Long idCiclo =
                planejamento.getCiclo() != null
                        ? planejamento
                        .getCiclo()
                        .getIdCiclo()
                        : null;

        return new PlanejamentoSemanalResponseDTO(
                planejamento.getIdPlanejamentoSemanal(),
                planejamento.getUsuario().getIdUsuario(),
                idCiclo,
                planejamento.getSemanaCiclo(),
                planejamento.getDataInicioSemana(),
                planejamento.getDataFimSemana(),
                totalPlanejadas,
                totalConcluidas,
                score,
                planejamento.getDataCriacao()
        );
    }

    private BigDecimal calcularScore(
            int totalPlanejadas,
            int totalConcluidas
    ) {

        if (totalPlanejadas == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal
                .valueOf(totalConcluidas)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(totalPlanejadas),
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private PlanejamentoTarefaResponseDTO
    converterTarefaParaResponse(
            PlanejamentoTarefa associacao
    ) {

        Tarefa tarefa =
                associacao.getTarefa();

        return new PlanejamentoTarefaResponseDTO(
                associacao.getIdPlanejamentoTarefa(),
                tarefa.getIdTarefa(),
                tarefa.getTitulo(),
                tarefa.getClassificacaoAbcde(),
                tarefa.getStatus(),
                tarefa.getTempoEstimado()
        );
    }
}