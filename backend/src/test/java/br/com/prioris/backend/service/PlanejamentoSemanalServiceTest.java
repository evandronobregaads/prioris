package br.com.prioris.backend.service;

import br.com.prioris.backend.dto.PlanejamentoSemanalResponseDTO;
import br.com.prioris.backend.entity.PlanejamentoSemanal;
import br.com.prioris.backend.entity.PlanejamentoTarefa;
import br.com.prioris.backend.entity.Tarefa;
import br.com.prioris.backend.entity.Usuario;
import br.com.prioris.backend.repository.CicloRepository;
import br.com.prioris.backend.repository.PlanejamentoSemanalRepository;
import br.com.prioris.backend.repository.PlanejamentoTarefaRepository;
import br.com.prioris.backend.repository.TarefaRepository;
import br.com.prioris.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanejamentoSemanalServiceTest {

    @Mock
    private PlanejamentoSemanalRepository planejamentoRepository;

    @Mock
    private PlanejamentoTarefaRepository planejamentoTarefaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CicloRepository cicloRepository;

    @Mock
    private TarefaRepository tarefaRepository;

    @InjectMocks
    private PlanejamentoSemanalService planejamentoService;

    @Test
    void deveCalcularScoreDeExecucaoEmCinquentaPorCento() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setAtivo(true);

        PlanejamentoSemanal planejamento =
                new PlanejamentoSemanal();

        planejamento.setIdPlanejamentoSemanal(2L);
        planejamento.setUsuario(usuario);
        planejamento.setSemanaCiclo((byte) 2);
        planejamento.setDataInicioSemana(
                LocalDate.of(2026, 8, 31)
        );
        planejamento.setDataFimSemana(
                LocalDate.of(2026, 9, 6)
        );

        Tarefa tarefaConcluida =
                new Tarefa();

        tarefaConcluida.setStatus("CONCLUIDA");

        Tarefa tarefaPendente =
                new Tarefa();

        tarefaPendente.setStatus("PENDENTE");

        PlanejamentoTarefa associacao1 =
                new PlanejamentoTarefa();

        associacao1.setPlanejamentoSemanal(
                planejamento
        );

        associacao1.setTarefa(
                tarefaConcluida
        );

        PlanejamentoTarefa associacao2 =
                new PlanejamentoTarefa();

        associacao2.setPlanejamentoSemanal(
                planejamento
        );

        associacao2.setTarefa(
                tarefaPendente
        );

        when(
                usuarioRepository
                        .findByIdUsuarioAndAtivoTrue(1L)
        ).thenReturn(
                Optional.of(usuario)
        );

        when(
                planejamentoRepository
                        .findByIdPlanejamentoSemanalAndUsuario_IdUsuario(
                                2L,
                                1L
                        )
        ).thenReturn(
                Optional.of(planejamento)
        );

        when(
                planejamentoTarefaRepository
                        .findAllByPlanejamentoSemanal_IdPlanejamentoSemanalOrderByIdPlanejamentoTarefaAsc(
                                2L
                        )
        ).thenReturn(
                List.of(
                        associacao1,
                        associacao2
                )
        );

        PlanejamentoSemanalResponseDTO resultado =
                planejamentoService.buscarPorId(
                        1L,
                        2L
                );

        assertEquals(
                2,
                resultado.getTotalTarefasPlanejadas()
        );

        assertEquals(
                1,
                resultado.getTotalTarefasConcluidas()
        );

        assertEquals(
                new BigDecimal("50.00"),
                resultado.getScoreExecucao()
        );
    }
}