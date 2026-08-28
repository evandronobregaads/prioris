package br.com.prioris.backend.service;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.entity.*;
import br.com.prioris.backend.exception.RecursoNaoEncontradoException;
import br.com.prioris.backend.exception.RevisaoSemanalJaExisteException;
import br.com.prioris.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class RevisaoSemanalService {

    private final RevisaoSemanalRepository revisaoRepository;
    private final PlanejamentoSemanalRepository planejamentoRepository;
    private final PlanejamentoTarefaRepository planejamentoTarefaRepository;
    private final UsuarioRepository usuarioRepository;

    public RevisaoSemanalService(
            RevisaoSemanalRepository revisaoRepository,
            PlanejamentoSemanalRepository planejamentoRepository,
            PlanejamentoTarefaRepository planejamentoTarefaRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.revisaoRepository = revisaoRepository;
        this.planejamentoRepository = planejamentoRepository;
        this.planejamentoTarefaRepository = planejamentoTarefaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public RevisaoSemanalResponseDTO cadastrar(
            Long idUsuario,
            Long idPlanejamento,
            RevisaoSemanalRequestDTO dto
    ) {

        buscarUsuarioAtivo(idUsuario);

        PlanejamentoSemanal planejamento =
                buscarPlanejamentoValido(
                        idUsuario,
                        idPlanejamento
                );

        if (revisaoRepository
                .existsByPlanejamentoSemanal_IdPlanejamentoSemanal(
                        idPlanejamento
                )) {

            throw new RevisaoSemanalJaExisteException(
                    "Já existe uma revisão para este planejamento semanal"
            );
        }

        BigDecimal score =
                calcularScoreExecucao(idPlanejamento);

        RevisaoSemanal revisao =
                new RevisaoSemanal();

        revisao.setPlanejamentoSemanal(planejamento);
        revisao.setScoreExecucao(score);
        revisao.setPrincipaisConquistas(
                dto.getPrincipaisConquistas().trim()
        );
        revisao.setDificuldades(
                dto.getDificuldades().trim()
        );
        revisao.setAjustesProximaSemana(
                dto.getAjustesProximaSemana().trim()
        );
        revisao.setObservacoes(
                dto.getObservacoes()
        );

        RevisaoSemanal revisaoSalva =
                revisaoRepository.save(revisao);

        return converterParaResponse(revisaoSalva);
    }

    public RevisaoSemanalResponseDTO buscar(
            Long idUsuario,
            Long idPlanejamento
    ) {

        buscarUsuarioAtivo(idUsuario);

        buscarPlanejamentoValido(
                idUsuario,
                idPlanejamento
        );

        RevisaoSemanal revisao =
                buscarRevisao(
                        idUsuario,
                        idPlanejamento
                );

        return converterParaResponse(revisao);
    }

    public List<RevisaoSemanalResponseDTO> listarHistorico(
            Long idUsuario
    ) {

        buscarUsuarioAtivo(idUsuario);

        return revisaoRepository
                .findAllByPlanejamentoSemanal_Usuario_IdUsuarioOrderByPlanejamentoSemanal_DataInicioSemanaDesc(
                        idUsuario
                )
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public RevisaoSemanalResponseDTO atualizar(
            Long idUsuario,
            Long idPlanejamento,
            RevisaoSemanalRequestDTO dto
    ) {

        buscarUsuarioAtivo(idUsuario);

        RevisaoSemanal revisao =
                buscarRevisao(
                        idUsuario,
                        idPlanejamento
                );

        revisao.setPrincipaisConquistas(
                dto.getPrincipaisConquistas().trim()
        );

        revisao.setDificuldades(
                dto.getDificuldades().trim()
        );

        revisao.setAjustesProximaSemana(
                dto.getAjustesProximaSemana().trim()
        );

        revisao.setObservacoes(
                dto.getObservacoes()
        );

        RevisaoSemanal revisaoAtualizada =
                revisaoRepository.save(revisao);

        return converterParaResponse(
                revisaoAtualizada
        );
    }

    public RevisaoSemanalResponseDTO atualizarParcialmente(
            Long idUsuario,
            Long idPlanejamento,
            RevisaoSemanalPatchDTO dto
    ) {

        buscarUsuarioAtivo(idUsuario);

        RevisaoSemanal revisao =
                buscarRevisao(
                        idUsuario,
                        idPlanejamento
                );

        if (dto.getPrincipaisConquistas() != null) {

            validarTexto(
                    dto.getPrincipaisConquistas(),
                    "As principais conquistas não podem ficar vazias"
            );

            revisao.setPrincipaisConquistas(
                    dto.getPrincipaisConquistas().trim()
            );
        }

        if (dto.getDificuldades() != null) {

            validarTexto(
                    dto.getDificuldades(),
                    "As dificuldades não podem ficar vazias"
            );

            revisao.setDificuldades(
                    dto.getDificuldades().trim()
            );
        }

        if (dto.getAjustesProximaSemana() != null) {

            validarTexto(
                    dto.getAjustesProximaSemana(),
                    "Os ajustes não podem ficar vazios"
            );

            revisao.setAjustesProximaSemana(
                    dto.getAjustesProximaSemana().trim()
            );
        }

        if (dto.getObservacoes() != null) {
            revisao.setObservacoes(
                    dto.getObservacoes()
            );
        }

        RevisaoSemanal revisaoAtualizada =
                revisaoRepository.save(revisao);

        return converterParaResponse(
                revisaoAtualizada
        );
    }

    public void excluir(
            Long idUsuario,
            Long idPlanejamento
    ) {

        buscarUsuarioAtivo(idUsuario);

        RevisaoSemanal revisao =
                buscarRevisao(
                        idUsuario,
                        idPlanejamento
                );

        revisaoRepository.delete(revisao);
    }

    private BigDecimal calcularScoreExecucao(
            Long idPlanejamento
    ) {

        List<PlanejamentoTarefa> tarefas =
                planejamentoTarefaRepository
                        .findAllByPlanejamentoSemanal_IdPlanejamentoSemanalOrderByIdPlanejamentoTarefaAsc(
                                idPlanejamento
                        );

        int totalPlanejadas = tarefas.size();

        if (totalPlanejadas == 0) {
            return BigDecimal.ZERO;
        }

        long totalConcluidas =
                tarefas
                        .stream()
                        .filter(associacao ->
                                "CONCLUIDA".equals(
                                        associacao
                                                .getTarefa()
                                                .getStatus()
                                )
                        )
                        .count();

        return BigDecimal
                .valueOf(totalConcluidas)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(totalPlanejadas),
                        2,
                        RoundingMode.HALF_UP
                );
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
                                "Planejamento semanal não encontrado com o id: "
                                        + idPlanejamento
                        )
                );
    }

    private RevisaoSemanal buscarRevisao(
            Long idUsuario,
            Long idPlanejamento
    ) {

        return revisaoRepository
                .findByPlanejamentoSemanal_IdPlanejamentoSemanalAndPlanejamentoSemanal_Usuario_IdUsuario(
                        idPlanejamento,
                        idUsuario
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Revisão semanal não encontrada"
                        )
                );
    }

    private void validarTexto(
            String valor,
            String mensagem
    ) {

        if (valor.trim().isBlank()) {
            throw new IllegalArgumentException(
                    mensagem
            );
        }
    }

    private RevisaoSemanalResponseDTO converterParaResponse(
            RevisaoSemanal revisao
    ) {

        PlanejamentoSemanal planejamento =
                revisao.getPlanejamentoSemanal();

        return new RevisaoSemanalResponseDTO(
                revisao.getIdRevisaoSemanal(),
                planejamento.getIdPlanejamentoSemanal(),
                planejamento.getSemanaCiclo(),
                planejamento.getDataInicioSemana(),
                planejamento.getDataFimSemana(),
                revisao.getScoreExecucao(),
                revisao.getPrincipaisConquistas(),
                revisao.getDificuldades(),
                revisao.getAjustesProximaSemana(),
                revisao.getObservacoes(),
                revisao.getDataRevisao()
        );
    }
}