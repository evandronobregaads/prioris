package br.com.prioris.backend.service;

import br.com.prioris.backend.dto.PrioridadeDiariaRequestDTO;
import br.com.prioris.backend.entity.PrioridadeDiaria;
import br.com.prioris.backend.entity.Usuario;
import br.com.prioris.backend.exception.PrioridadeDiariaJaDefinidaException;
import br.com.prioris.backend.repository.PrioridadeDiariaRepository;
import br.com.prioris.backend.repository.TarefaRepository;
import br.com.prioris.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrioridadeDiariaServiceTest {

    @Mock
    private PrioridadeDiariaRepository prioridadeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TarefaRepository tarefaRepository;

    @InjectMocks
    private PrioridadeDiariaService prioridadeService;

    @Test
    void deveRejeitarSegundaPrioridadeNoMesmoDia() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setAtivo(true);

        when(
                usuarioRepository.findByIdUsuarioAndAtivoTrue(1L)
        ).thenReturn(
                Optional.of(usuario)
        );

        when(
                prioridadeRepository
                        .existsByUsuario_IdUsuarioAndDataPrioridade(
                                eq(1L),
                                any(LocalDate.class)
                        )
        ).thenReturn(true);

        PrioridadeDiariaRequestDTO dto =
                new PrioridadeDiariaRequestDTO();

        dto.setIdTarefa(6L);

        PrioridadeDiariaJaDefinidaException exception =
                assertThrows(
                        PrioridadeDiariaJaDefinidaException.class,
                        () -> prioridadeService.definir(
                                1L,
                                dto
                        )
                );

        assertEquals(
                "Já existe uma Prioridade #1 definida para hoje",
                exception.getMessage()
        );

        verify(
                prioridadeRepository,
                never()
        ).save(
                any(PrioridadeDiaria.class)
        );

        verify(
                tarefaRepository,
                never()
        ).findByIdTarefaAndUsuario_IdUsuarioAndStatusNot(
                anyLong(),
                anyLong(),
                anyString()
        );
    }
}