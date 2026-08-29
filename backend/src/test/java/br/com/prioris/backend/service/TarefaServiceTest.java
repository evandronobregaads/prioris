package br.com.prioris.backend.service;

import br.com.prioris.backend.dto.TarefaRequestDTO;
import br.com.prioris.backend.entity.Tarefa;
import br.com.prioris.backend.entity.Usuario;
import br.com.prioris.backend.repository.MetaRepository;
import br.com.prioris.backend.repository.ObjetivoRepository;
import br.com.prioris.backend.repository.TarefaRepository;
import br.com.prioris.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MetaRepository metaRepository;

    @Mock
    private ObjetivoRepository objetivoRepository;

    @InjectMocks
    private TarefaService tarefaService;

    @Test
    void deveRejeitarTarefaComMetaEObjetivoAoMesmoTempo() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setAtivo(true);

        when(
                usuarioRepository.findByIdUsuarioAndAtivoTrue(1L)
        ).thenReturn(
                Optional.of(usuario)
        );

        TarefaRequestDTO dto = new TarefaRequestDTO();

        dto.setTitulo("Tarefa inválida");
        dto.setIdMeta(1L);
        dto.setIdObjetivo(1L);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> tarefaService.cadastrar(
                                1L,
                                dto
                        )
                );

        assertEquals(
                "A tarefa não pode estar vinculada a uma meta e a um objetivo ao mesmo tempo",
                exception.getMessage()
        );

        verify(
                tarefaRepository,
                never()
        ).save(
                any(Tarefa.class)
        );
    }
}